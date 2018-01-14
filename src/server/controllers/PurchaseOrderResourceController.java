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
import server.model.PurchaseOrderResource;
import server.repositories.PurchaseOrderResourceRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/purchaseorders/resources")
public class PurchaseOrderResourceController
{
    private PagedResourcesAssembler<PurchaseOrderResource> pagedAssembler;
    @Autowired
    private PurchaseOrderResourceRepository purchase_order_resourceRepository;

    @Autowired
    public PurchaseOrderResourceController(PagedResourcesAssembler<PurchaseOrderResource> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<PurchaseOrderResource>> getPurchaseOrderResource(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrderResource GET request id: "+ id);
        List<PurchaseOrderResource> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("purchase_order_id").is(id)), PurchaseOrderResource.class, "purchase_order_resources");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseOrderResource>> getPurchaseOrderResources(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrderResource GET request {all}");
        List<PurchaseOrderResource> contents =  IO.getInstance().mongoOperations().findAll(PurchaseOrderResource.class, "purchase_order_resources");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addPurchaseOrderResource(@RequestBody PurchaseOrderResource purchase_order_resource)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrderResource creation request");
        return APIController.putBusinessObject(purchase_order_resource, "purchase_order_resources", "purchase_orders_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchPurchaseOrderResource(@RequestBody PurchaseOrderResource purchase_order_resource)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrderResource update request.");
        return APIController.patchBusinessObject(purchase_order_resource, "purchase_order_resources", "purchase_orders_timestamp");
    }
}
