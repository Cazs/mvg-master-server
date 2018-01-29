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
import server.model.Notification;
import server.model.Notification;

import java.util.List;

@RepositoryRestController
@RequestMapping("/notifications")
public class NotificationController
{
    private PagedResourcesAssembler<Notification> pagedAssembler;

    @Autowired
    public NotificationController(PagedResourcesAssembler<Notification> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Notification>> getNotificationById(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling GET request for Notification: "+ id);
        List<Notification> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Notification.class, "notifications");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Notification>> getAllNotifications(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Notification get request {all}");
        List<Notification> contents =  IO.getInstance().mongoOperations().findAll(Notification.class, "notifications");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addNotification(@RequestBody Notification notification)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Notification creation request.");
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putMVGObject(notification, "notifications", "notifications_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchNotification(@RequestBody Notification notification)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Notification update request.");
        return APIController.patchMVGObject(notification, "notifications", "notifications_timestamp");
    }
}
