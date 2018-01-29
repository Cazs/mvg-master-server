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
import server.auxilary.IO;
import server.model.User;

import java.util.List;

@RepositoryRestController
@RequestMapping("/users")
public class UserController
{
    private PagedResourcesAssembler<User> pagedAssembler;

    @Autowired
    public UserController(PagedResourcesAssembler<User> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<User>> getUserById(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling GET request for User: "+ id);
        List<User> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), User.class, "users");
        if(contents!=null)//if not found by id, try by usr
        {
            if(contents.isEmpty())
            {
                IO.log(getClass().getName(), IO.TAG_WARN, "no User object matching _id found, trying usr..");
                contents = IO.getInstance().mongoOperations()
                        .find(new Query(Criteria.where("usr").is(id)), User.class, "users");
            } else  IO.log(getClass().getName(), IO.TAG_INFO, "found User object.");
        }
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling User get request {all}");
        List<User> contents =  IO.getInstance().mongoOperations().findAll(User.class, "users");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addUser(@RequestBody User user)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling User creation request.");
        //TODO: check access_level
        if(user!=null)
        {
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
                        if(user_creator.get(0).getAccess_level()>=AccessLevels.SUPERUSER.getLevel())
                        {
                            //user creating new superuser is a superuser, create new superuser
                            return APIController.putMVGObject(user, "users", "users_timestamp");
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
                    if(user.getAccess_level()==AccessLevels.ADMIN.getLevel())
                    {
                        //User to be created is first User in DB and is ADMIN account, throw error
                        IO.log(getClass().getName(), IO.TAG_ERROR, "Could not create ADMIN account. First account in the database must be a superuser.");
                        return new ResponseEntity<>("Could not create administrator account. First account in the database must be a superuser.", HttpStatus.NOT_FOUND);//superuser ID could not be found on db
                    } else //new User is the first superuser User, create User object
                        return APIController.putMVGObject(user, "users", "users_timestamp");
                }
            } else if(user.getAccess_level() == AccessLevels.NORMAL.getLevel())//if account to be created has access rights == STANDARD
            {
                //get User that's attempting to create the new Standard User object
                List<User> user_creator = IO.getInstance().mongoOperations().find(new
                        Query(Criteria.where("usr").is(user.getCreator())), User.class, "users");
                if(!user_creator.isEmpty())
                {
                    //found User, check if they're authorized to create STANDARD Users
                    if(user_creator.get(0).getAccess_level()>=AccessLevels.ADMIN.getLevel())
                    {
                        //user creating new STANDARD user is at least an ADMIN user, create user
                        return APIController.putMVGObject(user, "users", "users_timestamp");
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
                }
            } else //is a NO_ACCESS User, create object on db
                return APIController.putMVGObject(user, "users", "users_timestamp");
        } else
        {
            IO.log(getClass().getName(), IO.TAG_ERROR, "Invalid User object to be created.");
            return new ResponseEntity<>("Invalid User object to be created.", HttpStatus.CONFLICT);
        }
    }

    @PostMapping
    public ResponseEntity<String> patchUser(@RequestBody User user)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling User update request.");
        return APIController.patchMVGObject(user, "users", "users_timestamp");
    }
}
