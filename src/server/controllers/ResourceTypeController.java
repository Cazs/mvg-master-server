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
import server.model.ResourceType;
import server.repositories.ResourceTypeRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/resources/types")
public class ResourceTypeController
{
    private PagedResourcesAssembler<ResourceType> pagedAssembler;
    @Autowired
    private ResourceTypeRepository resource_typeRepository;

    @Autowired
    public ResourceTypeController(PagedResourcesAssembler<ResourceType> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<ResourceType>> getResourceType(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling ResourceType GET request id: "+ id);
        List<ResourceType> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), ResourceType.class, "resource_types");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ResourceType>> getResourceTypes(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling ResourceType GET request {all}");
        List<ResourceType> contents =  IO.getInstance().mongoOperations().findAll(ResourceType.class, "resource_types");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addResourceType(@RequestBody ResourceType resource_type)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling ResourceType creation request.");
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putMVGObject(resource_type, "resource_types", "resources_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchResource(@RequestBody ResourceType resource_type)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling ResourceType update request.");
        return APIController.patchMVGObject(resource_type, "resource_types", "resources_timestamp");
    }
}
