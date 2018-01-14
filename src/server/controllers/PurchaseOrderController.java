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
import server.model.FileMetadata;
import server.model.Job;
import server.model.PurchaseOrder;
import server.repositories.PurchaseOrderRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/purchaseorders")
public class PurchaseOrderController
{
    private PagedResourcesAssembler<PurchaseOrder> pagedAssembler;
    @Autowired
    private PurchaseOrderRepository purchase_orderRepository;

    @Autowired
    public PurchaseOrderController(PagedResourcesAssembler<PurchaseOrder> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<PurchaseOrder>> getPurchaseOrder(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrder GET request id: "+ id);
        List<PurchaseOrder> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), PurchaseOrder.class, "purchase_orders");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseOrder>> getPurchaseOrders(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrder GET request {all}");
        List<PurchaseOrder> contents =  IO.getInstance().mongoOperations().findAll(PurchaseOrder.class, "purchase_orders");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addPurchaseOrder(@RequestBody PurchaseOrder purchase_order)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrder creation request");
        return APIController.putBusinessObject(purchase_order, "purchase_orders", "purchase_orders_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchPurchaseOrder(@RequestBody PurchaseOrder purchase_order)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrder update request.");
        return APIController.patchBusinessObject(purchase_order, "purchase_orders", "purchase_orders_timestamp");
    }

    @PostMapping("/approval_request")//, consumes = "text/plain"//value =//, produces = "application/pdf"
    public ResponseEntity<String> requestPurchaseOrderApproval(@RequestHeader String purchaseorder_id, @RequestHeader String session_id,
                                                     @RequestHeader String message, @RequestHeader String subject,
                                                     @RequestBody FileMetadata fileMetadata)//, @RequestParam("file") MultipartFile file
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrder approval request.");
        return APIController.requestBusinessObjectApproval(purchaseorder_id, session_id, message, subject, fileMetadata, new PurchaseOrder().apiEndpoint(), PurchaseOrder.class);
    }

    @GetMapping("/approve/{purchaseorder_id}/{vericode}")
    public ResponseEntity<String> approvePurchaseOrder(@PathVariable("purchaseorder_id") String purchaseorder_id, @PathVariable("vericode") String vericode)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling PurchaseOrder "+purchaseorder_id+" approval request by Vericode.");
        return APIController.approveBusinessObjectByVericode(purchaseorder_id, vericode, "purchase_orders", "purchase_orders_timestamp", PurchaseOrder.class);
    }
}
