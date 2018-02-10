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
import server.model.FileMetadata;
import server.model.Trip;
import server.repositories.TripRepository;

import java.rmi.Remote;
import java.util.List;

@RepositoryRestController
public class TripController
{
    private PagedResourcesAssembler<Trip> pagedAssembler;
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    public TripController(PagedResourcesAssembler<Trip> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/trip/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Trip>> getTrip(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Trip GET request id: "+ id);
        List<Trip> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Trip.class, "trips");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    /**
     * Method to get Trips for a specific client/organisation.
     * @param id client/organisation identifier.
     * @param pageRequest
     * @param assembler
     * @return JSON array of Trips for that specific client/organisation.
     */
    @GetMapping(path="/trips/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Trip>> getTripsForClient(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Trip GET request for client with ID: "+ id);
        List<Trip> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("client_id").is(id)), Trip.class, "trips");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/trips")
    public ResponseEntity<Page<Trip>> getTrips(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Trip GET request {all}");
        List<Trip> contents =  IO.getInstance().mongoOperations().findAll(Trip.class, "trips");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping("/trips")
    public ResponseEntity<String> addTrip(@RequestBody Trip trip)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Trip creation request.");
        return APIController.putMVGObject(trip, "trips", "trips_timestamp");
    }

    @PostMapping("/trips")
    public ResponseEntity<String> patchTrip(@RequestBody Trip trip)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Trip update request.");
        return APIController.patchMVGObject(trip, "trips", "trips_timestamp");
    }

    @PostMapping(value = "/trips/approval_request")//, consumes = "text/plain"//value =//, produces = "application/pdf"
    public ResponseEntity<String> requestTripApproval(@RequestHeader String trip_id, @RequestHeader String session_id,
                                                       @RequestHeader String message, @RequestHeader String subject,
                                                       @RequestBody FileMetadata fileMetadata)//, @RequestParam("file") MultipartFile file
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Trip approval request.");
        return APIController.requestMVGObjectApproval(trip_id, session_id, message, subject, fileMetadata, new Trip().apiEndpoint(), Trip.class);
    }

    @GetMapping("/trips/approve/{trip_id}/{vericode}")
    public ResponseEntity<String> approveTrip(@PathVariable("trip_id") String trip_id, @PathVariable("vericode") String vericode)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Trip "+trip_id+" approval request by Vericode.");
        return APIController.approveMVGObjectByVericode(trip_id, vericode, "trips", "trips_timestamp", Trip.class);
    }
}
