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
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putBusinessObject(user, "users", "users_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchUser(@RequestBody User user)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling User update request.");
        return APIController.patchBusinessObject(user, "users", "users_timestamp");
    }
}
