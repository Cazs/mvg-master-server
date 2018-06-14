package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.MVGObject;
import server.model.AccommodationBooking;
import server.repositories.AccommodationBookingRepository;

@RepositoryRestController
public class AccommodationBookingController extends APIController
{
    private PagedResourcesAssembler<AccommodationBooking> pagedAssembler;
    @Autowired
    private AccommodationBookingRepository accommodationRepository;

    @Autowired
    public AccommodationBookingController(PagedResourcesAssembler<AccommodationBooking> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/bookings/accommodation/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getAccommodationBooking(@PathVariable("id") String id,
                                              @RequestHeader String session_id,
                                              Pageable pageRequest,
                                              PersistentEntityResourceAssembler assembler)
    {
        return get(new AccommodationBooking(id), "_id", session_id, "accommodation_bookings", pagedAssembler, assembler, pageRequest);
    }

    /**
     * Method to get AccommodationBookings for a specific client/organisation.
     * @param client_id client/organisation identifier.
     * @param pageRequest
     * @param assembler
     * @return JSON array of AccommodationBookings for that specific client/organisation.
     */
    @GetMapping(path="/bookings/accommodation/{client_id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getAccommodationBookingsForClient(@PathVariable("client_id") String client_id,
                                                                       @RequestHeader String session_id,
                                                                       Pageable pageRequest,
                                                                       PersistentEntityResourceAssembler assembler)
    {
        return get(new AccommodationBooking(client_id), "client_id", session_id, "accommodation_bookings", pagedAssembler, assembler, pageRequest);
    }

    @GetMapping("/bookings/accommodation")
    public ResponseEntity<Page<? extends MVGObject>> getAccommodationBookings(Pageable pageRequest,
                                                              @RequestHeader String session_id,
                                                              PersistentEntityResourceAssembler assembler)
    {
        return getAll(new AccommodationBooking(), session_id, "accommodation_bookings", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping("/bookings/accommodation")
    public ResponseEntity<String> addAccommodationBooking(@RequestBody AccommodationBooking accommodation_booking, @RequestHeader String session_id)
    {
        return put(accommodation_booking, session_id, "accommodation_bookings", "accommodation_bookings_timestamp");
    }

    @PostMapping("/bookings/accommodation")
    public ResponseEntity<String> patchAccommodationBooking(@RequestBody AccommodationBooking accommodation_booking, @RequestHeader String session_id)
    {
        return patch(accommodation_booking, session_id, "accommodation_bookings", "accommodation_bookings_timestamp");
    }
}
