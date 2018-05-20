package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.auxilary.IO;
import server.model.MVGObject;
import server.model.Metafile;
import server.model.TripBooking;
import server.repositories.TripBookingRepository;

@RepositoryRestController
public class TripBookingController extends APIController
{
    private PagedResourcesAssembler<TripBooking> pagedAssembler;
    @Autowired
    private TripBookingRepository tripRepository;

    @Autowired
    public TripBookingController(PagedResourcesAssembler<TripBooking> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/trip/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getTripBooking(@PathVariable("id") String id,
                                              @RequestHeader String session_id,
                                              Pageable pageRequest,
                                              PersistentEntityResourceAssembler assembler)
    {
        return get(new TripBooking(id), "_id", session_id, "trips", pagedAssembler, assembler, pageRequest);
    }

    /**
     * Method to get TripBookings for a specific client/organisation.
     * @param client_id client/organisation identifier.
     * @param pageRequest
     * @param assembler
     * @return JSON array of TripBookings for that specific client/organisation.
     */
    @GetMapping(path="/trips/{client_id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getTripBookingsForClient(@PathVariable("client_id") String client_id,
                                                                       @RequestHeader String session_id,
                                                                       Pageable pageRequest,
                                                                       PersistentEntityResourceAssembler assembler)
    {
        return get(new TripBooking(client_id), "client_id", session_id, "trips", pagedAssembler, assembler, pageRequest);
    }

    @GetMapping("/trips")
    public ResponseEntity<Page<? extends MVGObject>> getTripBookings(Pageable pageRequest,
                                                              @RequestHeader String session_id,
                                                              PersistentEntityResourceAssembler assembler)
    {
        return getAll(new TripBooking(), session_id, "trips", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping("/trip")
    public ResponseEntity<String> addTripBooking(@RequestBody TripBooking trip,
                                          @RequestHeader String session_id)
    {
        return put(trip, session_id, "trips", "trips_timestamp");
    }

    @PostMapping("/trips")
    public ResponseEntity<String> patchTripBooking(@RequestBody TripBooking trip,
                                            @RequestHeader String session_id)
    {
        return patch(trip, session_id, "trips", "trips_timestamp");
    }

    @PostMapping(value = "/trips/approval_request")//, consumes = "text/plain"//value =//, produces = "application/pdf"
    public ResponseEntity<String> requestTripBookingApproval(@RequestHeader String trip_id, @RequestHeader String session_id,
                                                       @RequestHeader String message, @RequestHeader String subject,
                                                       @RequestBody Metafile metafile)//, @RequestParam("file") MultipartFile file
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling TripBooking approval request.");
        return requestApproval(trip_id, session_id, message, subject, metafile, new TripBooking().apiEndpoint(), TripBooking.class);
    }

    @GetMapping("/trips/approve/{trip_id}/{vericode}")
    public ResponseEntity<String> approveTripBooking(@PathVariable("trip_id") String trip_id,
                                              @PathVariable("vericode") String vericode)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling TripBooking "+trip_id+" approval by Vericode.");
        return approveByVericode(trip_id, vericode, "trips", "trips_timestamp", TripBooking.class);
    }
}
