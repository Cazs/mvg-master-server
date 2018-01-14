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
import server.model.Leave;
import server.repositories.LeaveRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/leave_records")
public class LeaveController
{
    private PagedResourcesAssembler<Leave> pagedAssembler;
    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    public LeaveController(PagedResourcesAssembler<Leave> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Leave>> getLeave(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Leave GET request id: "+ id);
        List<Leave> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Leave.class, "leave_applications");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Leave>> getLeaveRecords(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Leave GET request {all}");
        List<Leave> contents =  IO.getInstance().mongoOperations().findAll(Leave.class, "leave_applications");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addLeaveRecord(@RequestBody Leave leave)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Leave record creation request");
        return APIController.putBusinessObject(leave, "leave_records", "leave_records_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchLeaveRecord(@RequestBody Leave leave)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Leave record update request.");
        return APIController.patchBusinessObject(leave, "leave_records", "leave_records_timestamp");
    }
}