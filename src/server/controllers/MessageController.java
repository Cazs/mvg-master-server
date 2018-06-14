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
import server.model.Message;
import server.repositories.MessageRepository;

import java.util.List;

@RepositoryRestController
public class MessageController extends APIController
{
    private PagedResourcesAssembler<Message> pagedAssembler;
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    public MessageController(PagedResourcesAssembler<Message> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/message/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getMessageById(@PathVariable("id") String id,
                                                                         @RequestHeader String session_id,
                                                                         Pageable pageRequest,
                                                                         PersistentEntityResourceAssembler assembler)
    {
        return get(new Message(id), "_id", session_id, "clients", pagedAssembler, assembler, pageRequest);
    }

    /**
     * Method to get Messages for a specific client/organisation.
     * @param id client/organisation identifier.
     * @param pageRequest
     * @param assembler
     * @return JSON array of Messages for that specific client/organisation.
     */
    @GetMapping(path="/messages/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Message>> getMessagesForClient(@PathVariable("id") String id,
                                                                   @RequestHeader String session_id,
                                                                   Pageable pageRequest,
                                                                   PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Message GET request for user with ID: "+ id);
        List<Message> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("creator").is(id)), Message.class, "messages"); // || new Query(Criteria.where("_id").is(id)
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/messages")
    public ResponseEntity<Page<? extends MVGObject>> getAllMessages(@RequestHeader String session_id,
                                                                  Pageable pageRequest,
                                                                  PersistentEntityResourceAssembler assembler)
    {
        return getAll(new Message(), session_id, "messages", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping("/message")
    public ResponseEntity<String> addMessage(@RequestHeader String session_id,
                                                  @RequestBody Message message)
    {
        return put(message, session_id, "messages", "messages_timestamp");
    }

    @PostMapping("/message")
    public ResponseEntity<String> patchMessage(@RequestHeader String session_id,
                                                    @RequestBody Message message)
    {
        return patch(message, session_id, "messages", "messages_timestamp");
    }
}
