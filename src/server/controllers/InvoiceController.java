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
import server.model.Invoice;
import server.repositories.InvoiceRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/invoices")
public class InvoiceController
{
    private PagedResourcesAssembler<Invoice> pagedAssembler;
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceController(PagedResourcesAssembler<Invoice> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Invoice>> getInvoice(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Invoice GET request id: "+ id);
        List<Invoice> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Invoice.class, "invoices");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Invoice>> getInvoices(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Invoice GET request {all}");
        List<Invoice> contents =  IO.getInstance().mongoOperations().findAll(Invoice.class, "invoices");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addInvoice(@RequestBody Invoice invoice)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Invoice creation request");
        return APIController.putMVGObject(invoice, "invoices", "invoices_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchInvoice(@RequestBody Invoice invoice)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Invoice update request.");
        return APIController.patchMVGObject(invoice, "invoices", "invoices_timestamp");
    }
}
