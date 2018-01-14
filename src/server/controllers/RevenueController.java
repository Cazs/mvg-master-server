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
import server.model.Revenue;
import server.repositories.RevenueRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/revenues")
public class RevenueController
{
    private PagedResourcesAssembler<Revenue> pagedAssembler;
    @Autowired
    private RevenueRepository revenueRepository;

    @Autowired
    public RevenueController(PagedResourcesAssembler<Revenue> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Revenue>> getRevenue(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Revenue GET request id: "+ id);
        List<Revenue> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Revenue.class, "revenues");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Revenue>> getRevenues(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Revenue GET request {all}");
        List<Revenue> contents =  IO.getInstance().mongoOperations().findAll(Revenue.class, "revenues");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addRevenue(@RequestBody Revenue revenue)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Revenue creation request.");
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putBusinessObject(revenue, "revenues", "revenues_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchRevenue(@RequestBody Revenue revenue)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Revenue update request.");
        return APIController.patchBusinessObject(revenue, "revenues", "revenues_timestamp");
    }
}