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
import server.model.TripDriver;
import server.repositories.TripUserRepository;
import java.util.List;

@RepositoryRestController
public class TripDriverController
{
    private PagedResourcesAssembler<TripDriver> pagedAssembler;

    @Autowired
    private TripUserRepository trip_userRepository;

    @Autowired
    public TripDriverController(PagedResourcesAssembler<TripDriver> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    //Trip User Route Handlers

    /**
     * Method to get drivers for a certain trips
     * @param id identifier for Trip whose drivers are to be acquired.
     * @param pageRequest
     * @param assembler
     * @return
     */
    @GetMapping(path="/trips/drivers/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<TripDriver>> getTripUser(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling TripDrivers GET request trip_id: ["+ id+"]");
        List<TripDriver> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("trip_id").is(id)), TripDriver.class, "trip_drivers");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/trips/drivers")
    public ResponseEntity<Page<TripDriver>> getTripDrivers(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling TripDrivers GET request {all}");
        List<TripDriver> contents =  IO.getInstance().mongoOperations().findAll(TripDriver.class, "trip_drivers");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping("/trips/drivers")
    public ResponseEntity<String> addTripUser(@RequestBody TripDriver trip_user)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling TripDrivers creation request: ");
        return APIController.putMVGObject(trip_user, "trip_drivers", "trips_timestamp");
    }
}
