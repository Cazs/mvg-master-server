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
import server.model.MVGObject;
import server.model.Notification;
import server.repositories.NotificationRepository;

import java.util.List;

@RepositoryRestController
public class NotificationController extends APIController
{
    private PagedResourcesAssembler<Notification> pagedAssembler;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    public NotificationController(PagedResourcesAssembler<Notification> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/notification/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getNotificationById(@PathVariable("id") String id,
                                                                         @RequestHeader String session_id,
                                                                         Pageable pageRequest,
                                                                         PersistentEntityResourceAssembler assembler)
    {
        return get(new Notification(id), "_id", session_id, "clients", pagedAssembler, assembler, pageRequest);
    }

    /**
     * Method to get Notifications for a specific client/organisation.
     * @param id client/organisation identifier.
     * @param pageRequest
     * @param assembler
     * @return JSON array of Notifications for that specific client/organisation.
     */
    @GetMapping(path="/notifications/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Notification>> getNotificationsForClient(@PathVariable("id") String id,
                                                                        @RequestHeader String session_id,
                                                                        Pageable pageRequest,
                                                                        PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Notification GET request for client with ID: "+ id);
        List<Notification> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("client_id").is(id)), Notification.class, "notifications");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/notifications")
    public ResponseEntity<Page<? extends MVGObject>> getAllNotifications(@RequestHeader String session_id,
                                                                  Pageable pageRequest,
                                                                  PersistentEntityResourceAssembler assembler)
    {
        return getAll(new Notification(), session_id, "notifications", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping("/notification")
    public ResponseEntity<String> addNotification(@RequestHeader String session_id,
                                                  @RequestBody Notification notification)
    {
        return put(notification, session_id, "notifications", "notifications_timestamp");
    }

    @PostMapping("/notification")
    public ResponseEntity<String> patchNotification(@RequestHeader String session_id,
                                                    @RequestBody Notification notification)
    {
        return patch(notification, session_id, "notifications", "notifications_timestamp");
    }
}
