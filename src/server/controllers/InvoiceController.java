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
import server.model.Metafile;
import server.model.Invoice;
import server.repositories.InvoiceRepository;

import java.util.List;

@RepositoryRestController
public class InvoiceController extends APIController
{
    private PagedResourcesAssembler<Invoice> pagedAssembler;
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceController(PagedResourcesAssembler<Invoice> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/invoice/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getInvoice(@PathVariable("id") String id,
                                                                @RequestHeader String session_id,
                                                                Pageable pageRequest,
                                                                PersistentEntityResourceAssembler assembler)
    {
        return get(new Invoice(id), "_id", session_id, "clients", pagedAssembler, assembler, pageRequest);
    }

    /**
     * Method to get a certain Client's Invoices.
     * @param id Client identifier.
     * @param pageRequest
     * @param assembler
     * @return JSON array representing Client's Invoices.
     */
    @GetMapping(path="/invoices/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Invoice>> getInvoicesForClient(@PathVariable("id") String id,
                                                              @RequestHeader String session_id,
                                                              Pageable pageRequest,
                                                              PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Invoices GET request for Client ID: "+ id);
        List<Invoice> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("client_id").is(id)), Invoice.class, "invoices");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/invoices")
    public ResponseEntity<Page<? extends MVGObject>> getInvoices(@RequestHeader String session_id,
                                                     Pageable pageRequest,
                                                     PersistentEntityResourceAssembler assembler)
    {
        return getAll(new Invoice(), session_id, "clients", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping("/invoice")
    public ResponseEntity<String> addInvoice(@RequestHeader String session_id, @RequestBody Invoice invoice)
    {
        return put(invoice, session_id, "invoices", "invoices_timestamp");
    }

    @PostMapping("/invoice")
    public ResponseEntity<String> patchInvoice(@RequestHeader String session_id, @RequestBody Invoice invoice)
    {
        return patch(invoice, session_id, "invoices", "invoices_timestamp");
    }
}
