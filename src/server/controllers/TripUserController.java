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
import server.auxilary.RemoteComms;
import server.exceptions.InvalidMVGObjectException;
import server.model.TripUser;
import server.repositories.TripUserRepository;
import java.rmi.Remote;
import java.util.List;

@RepositoryRestController
public class TripUserController
{
    private PagedResourcesAssembler<TripUser> pagedAssembler;

    @Autowired
    private TripUserRepository trip_userRepository;

    @Autowired
    public TripUserController(PagedResourcesAssembler<TripUser> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    //Trip User Route Handlers
    @GetMapping(path="/trips/users/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<TripUser>> getTripUser(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling TripUsers GET request trip_id: ["+ id+"]");
        List<TripUser> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("trip_id").is(id)), TripUser.class, "trip_users");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/trips/users")
    public ResponseEntity<Page<TripUser>> getTripUsers(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling TripUser GET request {all}");
        List<TripUser> contents =  IO.getInstance().mongoOperations().findAll(TripUser.class, "trip_users");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping("/trips/users")
    public ResponseEntity<String> addTripUser(@RequestBody TripUser trip_user)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling TripUser creation request: ");
        return APIController.putMVGObject(trip_user, "trip_users", "trips_timestamp");
    }
}
