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
import server.model.PurchaseOrderAsset;
import server.repositories.PurchaseOrderAssetRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/purchaseorders/assets")
public class PurchaseOrderAssetController
{
    private PagedResourcesAssembler<PurchaseOrderAsset> pagedAssembler;
    @Autowired
    private PurchaseOrderAssetRepository purchase_order_assetRepository;

    @Autowired
    public PurchaseOrderAssetController(PagedResourcesAssembler<PurchaseOrderAsset> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<PurchaseOrderAsset>> getPurchaseOrderAsset(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrderAsset GET request id: "+ id);
        List<PurchaseOrderAsset> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("purchase_order_id").is(id)), PurchaseOrderAsset.class, "purchase_order_assets");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseOrderAsset>> getPurchaseOrderAssets(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrderAsset GET request {all}");
        List<PurchaseOrderAsset> contents =  IO.getInstance().mongoOperations().findAll(PurchaseOrderAsset.class, "purchase_order_assets");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addPurchaseOrderAsset(@RequestBody PurchaseOrderAsset purchase_order_asset)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrderAsset creation request");
        return APIController.putMVGObject(purchase_order_asset, "purchase_order_assets", "purchase_orders_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchPurchaseOrderAsset(@RequestBody PurchaseOrderAsset purchase_order_asset)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrderAsset update request.");
        return APIController.patchMVGObject(purchase_order_asset, "purchase_order_assets", "purchase_orders_timestamp");
    }
}
