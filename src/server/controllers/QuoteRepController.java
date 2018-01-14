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
import server.auxilary.RemoteComms;
import server.exceptions.InvalidBusinessObjectException;
import server.exceptions.InvalidQuoteException;
import server.model.Counter;
import server.model.Quote;
import server.model.QuoteRep;
import server.repositories.QuoteRepRepository;

import java.rmi.Remote;
import java.util.List;

@RepositoryRestController
//@RequestMapping("/quotes/representatives")
public class QuoteRepController
{
    private PagedResourcesAssembler<QuoteRep> pagedAssembler;
    @Autowired
    private QuoteRepRepository quote_representativeRepository;

    @Autowired
    public QuoteRepController(PagedResourcesAssembler<QuoteRep> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/quotes/representatives/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<QuoteRep>> getQuoteRep(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling QuoteRep GET request id: "+ id);
        List<QuoteRep> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("quote_id").is(id)), QuoteRep.class, "quote_representatives");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/quotes/representatives")
    public ResponseEntity<Page<QuoteRep>> getQuoteReps(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling QuoteRep GET request {all}");
        List<QuoteRep> contents =  IO.getInstance().mongoOperations().findAll(QuoteRep.class, "quote_representatives");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    //public ResponseEntity<Page<QuoteRep>> addQuoteRep(@RequestBody QuoteRep quote_rep, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    @PutMapping("/quotes/representatives")
    public ResponseEntity<String> addQuoteRep(@RequestBody QuoteRep quote_rep)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling QuoteRep creation request.");
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putBusinessObject(quote_rep, "quote_representatives", "quotes_timestamp");
    }

    @PostMapping("/quotes/representatives")
    public ResponseEntity<String> patchQuote(@RequestBody QuoteRep quote_rep)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling QuoteRep update request.");
        return APIController.patchBusinessObject(quote_rep, "quote_representatives", "quotes_timestamp");
    }
}
