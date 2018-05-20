package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.auxilary.BCrypt;
import server.auxilary.IO;
import server.auxilary.RemoteComms;
import server.auxilary.Session;
import server.exceptions.UserNotFoundException;
import server.managers.SessionManager;
import server.model.Counter;
import server.model.MVGObject;
import server.model.Metafile;
import server.model.User;

import java.util.List;

/**
 * Contains behaviour for retrieving timestamps and user authentication etc..
 * Created by th3gh0st on 2018/05/20.
 * @author th3gh0st
 */

@RestController
@RequestMapping("/")
public class RootController
{
    private PagedResourcesAssembler<MVGObject> pagedAssembler;

    @Autowired
    public RootController(PagedResourcesAssembler<MVGObject> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping
    public String root()
    {
        IO.log(getClass().getName(), IO.TAG_VERBOSE, "handling API root get request.");
        return "You have requested the API's root page, this does absolutely nothing.";
    }

    @GetMapping(path="/timestamp/{id}", produces = "application/hal+json")
    public Counter getTimestamp(@PathVariable("id") String id)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "handling GET request for Counter: "+ id);
        return CounterController.getCounter(id);
    }

    @PutMapping("/session")
    public ResponseEntity<Page<? extends MVGObject>> auth(@RequestHeader String usr, @RequestHeader String pwd)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "handling auth request ["+usr+":"+pwd+"]");
        String session_id = null;
        List<User> users =  IO.getInstance().mongoOperations().find(
                new Query(Criteria.where("usr").is(usr)), User.class, "users");
        if(users!=null)
        {
            boolean found = false;
            for(User user: users)
                if(BCrypt.checkpw(pwd, user.getPwd()))
                {
                    found = true;
                    break;
                }
            if(found)
            {
                IO.log(getClass().getName(), IO.TAG_VERBOSE, "correct credentials.");
                session_id = IO.generateRandomString(16);
                //found valid usr to pwd match, create session
                Session session = new Session();
                session.setSession_id(session_id);
                session.setUsr(usr);
                session.setDate(System.currentTimeMillis());
                session.setTtl(RemoteComms.TTL);
                SessionManager.getInstance().addSession(session);
                IO.log(getClass().getName(), IO.TAG_INFO, "user ["+session.getUsr()+"] signed in.");

                return new ResponseEntity(session, HttpStatus.OK);
            } else if(users.size()!=1)
                throw new UserNotFoundException();
        } else // List is null, no users were found
            throw new UserNotFoundException();
        return new ResponseEntity("Invalid user credentials.", HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/mailto")
    public ResponseEntity<String> emailQuote(@RequestHeader String document_id, @RequestHeader String session_id,
                                             @RequestHeader String message, @RequestHeader String subject,
                                             @RequestHeader String destination, @RequestBody Metafile metafile)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling mailto request.");
        return APIController.email(document_id, session_id, message, subject, destination, metafile);
    }
}
