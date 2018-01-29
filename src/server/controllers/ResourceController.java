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
import server.model.Resource;
import server.repositories.ResourceRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/resources")
public class ResourceController
{
    private PagedResourcesAssembler<Resource> pagedAssembler;
    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    public ResourceController(PagedResourcesAssembler<Resource> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Resource>> getResource(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Resource GET request id: "+ id);
        List<Resource> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Resource.class, "resources");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Resource>> getResources(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Resource GET request {all}");
        List<Resource> contents =  IO.getInstance().mongoOperations().findAll(Resource.class, "resources");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addResource(@RequestBody Resource resource)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Resource creation request.");
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putMVGObject(resource, "resources", "resources_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchResource(@RequestBody Resource resource)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Resource update request.");
        return APIController.patchMVGObject(resource, "resources", "resources_timestamp");
    }
}
