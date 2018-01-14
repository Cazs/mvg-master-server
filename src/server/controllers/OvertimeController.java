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
import server.model.Overtime;
import server.repositories.OvertimeRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/overtime_records")
public class OvertimeController
{
    private PagedResourcesAssembler<Overtime> pagedAssembler;
    @Autowired
    private OvertimeRepository overtimeRepository;

    @Autowired
    public OvertimeController(PagedResourcesAssembler<Overtime> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Overtime>> getOvertime(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Overtime GET request id: "+ id);
        List<Overtime> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Overtime.class, "overtime_applications");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Overtime>> getOvertimeRecords(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Overtime GET request {all}");
        List<Overtime> contents =  IO.getInstance().mongoOperations().findAll(Overtime.class, "overtime_applications");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addOvertimeRecords(@RequestBody Overtime overtime)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Overtime record creation request");
        return APIController.putBusinessObject(overtime, "overtime_records", "overtime_records_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchOvertime(@RequestBody Overtime overtime)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Overtime record update request.");
        return APIController.patchBusinessObject(overtime, "overtime_records", "overtime_records_timestamp");
    }
}
