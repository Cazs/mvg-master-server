package server.controllers;

import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.auxilary.AccessLevels;
import server.auxilary.IO;
import server.auxilary.RemoteComms;
import server.auxilary.Session;
import server.exceptions.UserNotFoundException;
import server.exceptions.InvalidMVGObjectException;
import server.managers.SessionManager;
import server.model.*;

import java.rmi.Remote;
import java.util.List;

@RestController
@RequestMapping("/")
public class APIController
{
    @GetMapping
    public String root()
    {
        IO.log(getClass().getName(), IO.TAG_VERBOSE, "handling API root get request.");
        return "You have requested API root page, this does absolutely nothing.";
    }

    @GetMapping(path="/timestamp/{id}", produces = "application/hal+json")
    public Counter getTimestamp(@PathVariable("id") String id)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "handling GET request for Counter: "+ id);
        return CounterController.getCounter(id);
    }

    @PutMapping("/auth")
    public String auth(@RequestHeader String usr, @RequestHeader String pwd)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "handling auth request.");
        String session_id = null;
        List<User> users =  IO.getInstance().mongoOperations().find(new Query(Criteria.where("usr").is(usr).and("pwd").is(pwd)), User.class, "users");
        if(users!=null)
        {
            if(users.size()==1)
            {
                session_id = IO.generateRandomString(16);
                //found valid usr to pwd match, create session
                Session session = new Session();
                session.setSession_id(session_id);
                session.setUsr(usr);
                session.setDate(System.currentTimeMillis());
                session.setTtl(RemoteComms.TTL);
                SessionManager.getInstance().addSession(session);
                IO.log(getClass().getName(), IO.TAG_INFO, "user ["+session.getUsr()+"] signed in.");
                return session.toString();
            } else if(users.size()!=1)
                throw new UserNotFoundException();
        } else//List is null, no users found
            throw new UserNotFoundException();
        return "Incorrect User credentials.";
    }

    public static ResponseEntity<String> emailMVGObject(String _id, String session_id, String message,
                                                             String subject, String destination, FileMetadata fileMetadata, Class model)//, @RequestParam("file") MultipartFile file
    {
        if(fileMetadata==null)
            return new ResponseEntity<>("Invalid attached FileMetadata object.", HttpStatus.CONFLICT);
        if(session_id!=null)
        {
            Session user_session = SessionManager.getInstance().getUserSession(session_id);
            if(user_session!=null)
            {
                if(_id!=null)
                {
                    if(fileMetadata.getFile()!=null)
                    {
                        try
                        {
                            //check if destination param contains multiple emails
                            String[] email_addresses = destination.split(",");
                            //Send email
                            MailjetResponse response = RemoteComms.emailWithAttachment(subject, message, email_addresses, new FileMetadata[]{fileMetadata});
                            if(response==null)
                                return new ResponseEntity<>("Could not send email.", HttpStatus.CONFLICT);
                            else return new ResponseEntity<>("eMail has been sent successfully.", HttpStatus.valueOf(response.getStatus()));
                        } catch (MailjetSocketTimeoutException e)
                        {
                            IO.log(APIController.class.getName(), IO.TAG_ERROR, e.getMessage());
                            return new ResponseEntity<>("Could not send eMail: "+e.getMessage(), HttpStatus.CONFLICT);
                        } catch (MailjetException e)
                        {
                            IO.log(APIController.class.getName(), IO.TAG_ERROR, e.getMessage());
                            return new ResponseEntity<>("Could not send eMail: "+e.getMessage(), HttpStatus.CONFLICT);
                        }
                    } else return new ResponseEntity<>("Invalid attached MVGObject{"+model.getName()+"["+_id+"]} PDF", HttpStatus.CONFLICT);
                } else return new ResponseEntity<>("Invalid _id param", HttpStatus.CONFLICT);
            } else return new ResponseEntity<>("Invalid user session. Please log in.", HttpStatus.CONFLICT);
        } else return new ResponseEntity<>("Invalid session_id header param", HttpStatus.CONFLICT);
    }

    public static ResponseEntity<String> requestMVGObjectApproval(String _id, String session_id, String message,
                                                                       String subject, FileMetadata fileMetadata, String endpoint, Class model)//, @RequestParam("file") MultipartFile file
    {
        if(fileMetadata==null)
            return new ResponseEntity<>("Invalid attached FileMetadata object.", HttpStatus.CONFLICT);
        if(session_id!=null)
        {
            Session user_session = SessionManager.getInstance().getUserSession(session_id);
            if(user_session!=null)
            {
                if(_id!=null)
                {
                    if(fileMetadata.getFile()!=null)
                    {
                        IO.log(APIController.class.getClass().getName(), IO.TAG_INFO, "creating Vericode for MVGObject{"+model.getName()+"["+_id+"]}");
                        //create Vericode object
                        String approval_code = IO.generateRandomString(16);
                        Vericode vericode = new Vericode(_id, approval_code);
                        vericode.setDate_logged(System.currentTimeMillis());
                        vericode.setCreator(user_session.getUsr());

                        //save Vericode object
                        String new_vericode_id = APIController.putMVGObject(vericode, "vericodes", "vericodes_timestamp").getBody();

                        try
                        {
                            IO.log(APIController.class.getName(), IO.TAG_INFO, "Looking for Users with approval clearance.");
                            //look for Users with enough clearance for approval
                            List<User> auth_users = IO.getInstance().mongoOperations().find(
                                    new Query(Criteria.where("access_level").gte(AccessLevels.SUPERUSER.getLevel())),
                                    User.class, "users");

                            //check if any Users were found
                            if(auth_users==null)
                                return new ResponseEntity<>("Could not find any Users with approval clearance.", HttpStatus.CONFLICT);
                            if(auth_users.isEmpty())
                                return new ResponseEntity<>("Could not find any Users with approval clearance.", HttpStatus.CONFLICT);

                            IO.log(APIController.class.getName(), IO.TAG_INFO, "Found ["+auth_users.size()+"] Users with clearance. Sending[eMailing] MVGObject{"+model.getName()+"["+_id+"]} for approval.");
                            User[] auth_users_arr = new User[auth_users.size()];
                            auth_users.toArray(auth_users_arr);

                            //create approval link
                            message += "<br/><h3 style=\"text-align:center;\">"
                                    +"Click <a href=\""
                                    +RemoteComms.host+endpoint+"/approve/"+_id+"/"+vericode.getCode()
                                    +"\">here</a> to approve this "+model.getSimpleName()+".</h3>";
                            //Send email with approval link
                            MailjetResponse response = RemoteComms.emailWithAttachment(subject, message,
                                    auth_users_arr, new FileMetadata[]{fileMetadata});
                            if(response==null)
                                return new ResponseEntity<>("Could not send email for approval.", HttpStatus.CONFLICT);
                            else return new ResponseEntity<>(String.valueOf(response.getStatus()), HttpStatus.valueOf(response.getStatus()));
                        } catch (MailjetSocketTimeoutException e)
                        {
                            IO.log(APIController.class.getName(), IO.TAG_ERROR, e.getMessage());
                            return new ResponseEntity<>("Could not send eMail: "+e.getMessage(), HttpStatus.CONFLICT);
                        } catch (MailjetException e)
                        {
                            IO.log(APIController.class.getName(), IO.TAG_ERROR, e.getMessage());
                            return new ResponseEntity<>("Could not send eMail: "+e.getMessage(), HttpStatus.CONFLICT);
                        }
                    } else return new ResponseEntity<>("Invalid attached MVGObject{"+model.getName()+"["+_id+"]} PDF", HttpStatus.CONFLICT);
                } else return new ResponseEntity<>("Invalid _id param", HttpStatus.CONFLICT);
            } else return new ResponseEntity<>("Invalid user session. Please log in.", HttpStatus.CONFLICT);
        } else return new ResponseEntity<>("Invalid session_id header param", HttpStatus.CONFLICT);
    }

    public static ResponseEntity<String> approveMVGObjectByVericode(String _id, String vericode, String collection, String collection_timestamp, Class model)
    {
        List<Vericode> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("code_name").is(_id).and("code").is(vericode)), Vericode.class, "vericodes");
        String response_msg = "<!DOCTYPE html><html>";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "text/html");
        if(contents!=null)
        {
            if (!contents.isEmpty())
            {
                //valid Vericode-MVGObject combination - approve MVGObject
                List<MVGObject> MVGObjects = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(_id)), model, collection);
                if (MVGObjects != null)
                {
                    if (!MVGObjects.isEmpty())
                    {
                        IO.log(APIController.class.getName(), IO.TAG_INFO, "valid MVGObject{"+model+"["+_id+"]} approval credentials. Updating status.");

                        MVGObject MVGObject = MVGObjects.get(0);
                        MVGObject.parse("status", MVGObject.STATUS_APPROVED);
                        HttpStatus status = APIController.patchMVGObject(MVGObject, collection, collection_timestamp).getStatusCode();

                        if(status==HttpStatus.OK)
                        {
                            IO.log(APIController.class.getClass().getName(), IO.TAG_INFO, "Successfully approved MVGObject{"+model+"["+_id+"]} using Vericode ["+vericode+"].");
                            response_msg += "<h3>Successfully approved {"+model+"["+_id+"]} using verification code [" + vericode + "]</h3>"
                                    + "<script>alert('Successfully approved {"+model+"["+_id+"]} using verification code [" + vericode + "]');</script></html>";
                            return new ResponseEntity(response_msg, httpHeaders, HttpStatus.OK);
                            //return response_msg;
                        } else response_msg += "<h3>Could not approve {"+model+"["+_id+"]}.</h3>"
                                + "<script>alert('Could not approve {"+model+"["+_id+"]}');</script></html>";
                    }
                    else response_msg += "<h3>Could not find any {"+model+"["+_id+"]} matching provided id.</h3>"
                            + "<script>alert('Could not find any {"+model+"["+_id+"]} matching provided id.');</script></html>";
                }
                else response_msg += "<h3>Invalid {"+model+"["+_id+"]} to be approved.</h3>"
                        + "<script>alert('Invalid {"+model+"["+_id+"]} to be approved.');</script></html>";
            }
            else response_msg += "<h3>Could not find any {"+model+"["+_id+"]} verification codes matching provided credentials.</h3>"
                    + "<script>alert('Could not find any {"+model+"["+_id+"]} verification codes matching provided credentials.');</script></html>";
        } else response_msg += "<h3>Invalid {"+model+"["+_id+"]} approval credentials.</h3>"
                + "<script>alert('Invalid {"+model+"["+_id+"]} approval credentials.');</script></html>";

        IO.log(APIController.class.getClass().getName(), IO.TAG_ERROR, "Could not approve {"+model+"["+_id+"]} using verification code [" + vericode + "]: " + response_msg);
        return new ResponseEntity(response_msg, httpHeaders, HttpStatus.NOT_FOUND);
        //@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="");
        //return response_msg;
    }

    public static ResponseEntity<String> putMVGObject(MVGObject MVGObject, String collection, String collection_timestamp)
    {
        if(MVGObject!=null)
        {
            /*if(MVGObject.get_id()==null)
            {
                IO.log(Remote.class.getName(),IO.TAG_ERROR, "invalid "+MVGObject.getClass().getName()+" _id attribute.");
                return new ResponseEntity<>("invalid "+MVGObject.getClass().getName()+" _id attribute.", HttpStatus.CONFLICT);
            }*/

            IO.log(APIController.class.getName(), IO.TAG_INFO, "attempting to create new MVGObject ["+MVGObject.getClass().getName()+"]: "+MVGObject.toString()+"");
            try
            {
                MVGObject.setDate_logged(System.currentTimeMillis());
                RemoteComms.commitMVGObjectToDatabase(MVGObject, collection, collection_timestamp);
                return new ResponseEntity<>(MVGObject.get_id(), HttpStatus.OK);
            } catch (InvalidMVGObjectException e)
            {
                IO.log(Remote.class.getName(),IO.TAG_ERROR, "invalid "+MVGObject.getClass().getName()+" object: {"+e.getMessage()+"}");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>("Invalid MVGObject", HttpStatus.CONFLICT);
    }

    public static ResponseEntity<String> patchMVGObject(MVGObject MVGObject, String collection, String collection_timestamp)
    {
        if(MVGObject!=null)
        {
            if(MVGObject.get_id()==null)
            {
                IO.log(Remote.class.getName(),IO.TAG_ERROR, "invalid "+MVGObject.getClass().getName()+" _id attribute.");
                return new ResponseEntity<>("invalid "+MVGObject.getClass().getName()+" _id attribute.", HttpStatus.CONFLICT);
            }

            IO.log(APIController.class.getName(), IO.TAG_INFO, "patching "+MVGObject.getClass().getName()+" ["+MVGObject.get_id()+"]");
            try
            {
                RemoteComms.commitMVGObjectToDatabase(MVGObject, collection, collection_timestamp);
                return new ResponseEntity<>(MVGObject.get_id(), HttpStatus.OK);
            } catch (InvalidMVGObjectException e)
            {
                IO.log(Remote.class.getName(),IO.TAG_ERROR, "invalid "+MVGObject.getClass().getName()+" object: {"+e.getMessage()+"}");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>("Invalid MVGObject", HttpStatus.CONFLICT);
    }
}
