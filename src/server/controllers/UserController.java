package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.auxilary.AccessLevels;
import server.auxilary.BCrypt;
import server.auxilary.IO;
import server.model.MVGObject;
import server.model.User;

import java.util.List;

@RepositoryRestController
public class UserController extends APIController
{
    private PagedResourcesAssembler<User> pagedAssembler;

    @Autowired
    public UserController(PagedResourcesAssembler<User> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/user/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getUserById(@PathVariable("id") String id, @RequestHeader String session_id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        return get(new User(id), "_id", session_id, "users", pagedAssembler, assembler, pageRequest);
    }

    @GetMapping(path="/username/{usr}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getUserByUsername(@PathVariable("usr") String usr, @RequestHeader String session_id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        return get(new User().setUsr(usr), "usr", session_id, "users", pagedAssembler, assembler, pageRequest);
    }

    @GetMapping(path = "/users")
    public ResponseEntity<Page<? extends MVGObject>> getAllUsers(Pageable pageRequest, @RequestHeader String session_id, PersistentEntityResourceAssembler assembler)
    {
        return getAll(new User(), session_id, "users", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping("/user")
    public ResponseEntity<String> addUser(@RequestBody User user, @RequestHeader String session_id)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling User creation request.");
        //TODO: check access_level
        if(user!=null)
        {
            // hash user password
            String hashed_pwd = BCrypt.hashpw(user.getPwd(), BCrypt.gensalt(12));
            IO.log(getClass().getName(), IO.TAG_INFO, "hashed pwd ["+hashed_pwd+"]");
            user.setPwd(hashed_pwd);

            //only superusers can create accounts with access_level >= ADMIN
            if(user.getAccess_level()>= AccessLevels.ADMIN.getLevel())//if account to be created has access rights >= ADMIN
            {
                //check if a super user account already exists in the database
                List<User> superusers = IO.getInstance().mongoOperations().find(new Query(Criteria.where("access_level").gte(AccessLevels.SUPERUSER.getLevel())), User.class, "users");
                if(!superusers.isEmpty())
                {
                    //get user that's attempting to create the new superuser object
                    List<User> user_creator = IO.getInstance().mongoOperations().find(new
                                                                                                      Query(Criteria.where("usr").is(user.getCreator())), User.class, "users");
                    if(!user_creator.isEmpty())
                    {
                        //found user, check if they're authorized to create superusers
                        if(user_creator.get(0).getAccess_level()>= AccessLevels.SUPERUSER.getLevel())
                        {
                            //user creating new superuser is a superuser, create new superuser
                            return put(user, session_id, "users", "users_timestamp");
                        } else
                        {
                            IO.log(getClass().getName(), IO.TAG_ERROR, "User ["+user_creator.get(0).getName()+"] is not authorised to create users with this level of access. Can't create ADMIN/SUPER account.");
                            return new ResponseEntity<>("User ["+user_creator.get(0).getName()+"] is not authorised to create users with this level of access. Please log in with an existing superuser account to create superusers and administrators.", HttpStatus.CONFLICT);
                        }
                    } else
                    {
                        IO.log(getClass().getName(), IO.TAG_ERROR, "Could not find user that created this user object. Can't create ADMIN/SUPER account.");
                        return new ResponseEntity<>("You are not authorised to create users with this level of access. Please log in with an existing superuser account to create superusers and administrators.", HttpStatus.NOT_FOUND);//superuser ID could not be found on db
                    }
                } else//no superusers in DB
                {
                    if(user.getAccess_level()== AccessLevels.ADMIN.getLevel())
                    {
                        //User to be created is first User in DB and is ADMIN account, throw error
                        IO.log(getClass().getName(), IO.TAG_ERROR, "Could not create ADMIN account. First account in the database must be a superuser.");
                        return new ResponseEntity<>("Could not create administrator account. First account in the database must be a superuser.", HttpStatus.NOT_FOUND);//superuser ID could not be found on db
                    } else //new User is the first superuser User, create User object
                        return put(user, session_id, "users", "users_timestamp");
                }
            } else if(user.getAccess_level() == AccessLevels.STANDARD.getLevel())//if account to be created has access rights == STANDARD
            {
                return put(user, session_id, "users", "users_timestamp");
                //get User that's attempting to create the new Standard User object
                /* List<User> user_creator = IO.getInstance().mongoOperations().find(new
                        Query(Criteria.where("usr").is(user.getCreator())), User.class, "users");
                if(!user_creator.isEmpty())
                {
                    //found User, check if they're authorized to create STANDARD Users
                    if(user_creator.get(0).getAccess_level()>= AccessLevels.ADMIN.getLevel())
                    {
                        //user creating new STANDARD user is at least an ADMIN user, create user
                        return putBusinessObject(user, session_id, "users", "users_timestamp");
                    } else
                    {
                        //user creating new STANDARD user is not authorised to create user
                        IO.log(getClass().getName(), IO.TAG_ERROR, "User ["+user_creator.get(0).getName()+"] is not authorised to create users with this level of access [STANDARD].");
                        return new ResponseEntity<>("User ["+user_creator.get(0).getName()+"] is not authorised to create users with this level of access. Please log in with an administrator/superuser account to create standard user accounts.", HttpStatus.CONFLICT);
                    }
                } else
                {
                    IO.log(getClass().getName(), IO.TAG_ERROR, "Could not find account of User that's creating the new STANDARD User account.");
                    return new ResponseEntity<>("You are not authorised to create users with this level of access. Please log in with an administrator/superuser account to create standard user accounts.", HttpStatus.NOT_FOUND);//superuser ID could not be found on db
                }*/
            } else //is a NO_ACCESS User, create object on db
                return put(user, session_id, "users", "users_timestamp");
        } else
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "Invalid User object to be created.");
            return new ResponseEntity<>("Invalid User object to be created.", HttpStatus.CONFLICT);
        }
    }

    @PostMapping(path = "/user")
    public ResponseEntity<String> patchUser(@RequestBody User user, @RequestHeader String session_id)
    {
        return patch(user, session_id, "users", "users_timestamp");
    }

    /**
     * never delete by username
     * */
    @DeleteMapping(path = "/user/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable String user_id, @RequestHeader String session_id)
    {
        return delete(new User(user_id), session_id, "users", "users_timestamp");
    }
}
