package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.AccommodationDestination;
import server.model.MVGObject;
import server.repositories.AccommodationDestinationRepository;

/**
 * Created by th3gh0st on 2018/05/22.
 * @author th3gh0st
 */
@RepositoryRestController
public class AccommodationDestinationController extends APIController
{
     private PagedResourcesAssembler<AccommodationDestination> pagedAssembler;
     @Autowired
     private AccommodationDestinationRepository destinationRepository;

    @Autowired
    public AccommodationDestinationController(PagedResourcesAssembler<AccommodationDestination> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/destination/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getAccommodationDestination(@PathVariable("id") String id,
                                                                         @RequestHeader String session_id,
                                                                         Pageable pageRequest,
                                                                         PersistentEntityResourceAssembler assembler)
    {
        return get(new AccommodationDestination(id), "_id", session_id, "accommodation_destinations", pagedAssembler, assembler, pageRequest);
    }

    @GetMapping("/destinations")
    public ResponseEntity<Page<? extends MVGObject>> getAccommodationDestinations(@RequestHeader String session_id,
                                                                          Pageable pageRequest,
                                                                          PersistentEntityResourceAssembler assembler)
    {
        return getAll(new AccommodationDestination(), session_id, "accommodation_destinations", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping("/destination")
    public ResponseEntity<String> addAccommodationDestination(@RequestBody AccommodationDestination destination, @RequestHeader String session_id)
    {
        return put(destination, session_id, "accommodation_destinations", "accommodation_destinations_timestamp");
    }

    @PostMapping("/destination")
    public ResponseEntity<String> patchAccommodationDestination(@RequestBody AccommodationDestination destination, @RequestHeader String session_id)
    {
        return patch(destination, session_id, "accommodation_destinations", "accommodation_destinations_timestamp");
    }

    @DeleteMapping(path="/destination/{destination_id}")
    public ResponseEntity<String> delete(@PathVariable String destination_id, @RequestHeader String session_id)
    {
        return delete(new AccommodationDestination(destination_id), session_id, "accommodation_destinations", "accommodation_destinations_timestamp");
    }
}
