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
import server.model.*;
import server.repositories.QuoteRepository;

import java.util.List;

@RepositoryRestController
public class QuoteController extends APIController
{
    private PagedResourcesAssembler<Quote> pagedAssembler;
    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    public QuoteController(PagedResourcesAssembler<Quote> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/quote/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getQuote(  @PathVariable("id") String id,
                                                                @RequestHeader String session_id,
                                                                Pageable pageRequest,
                                                                PersistentEntityResourceAssembler assembler)
    {
        return get(new Quote(id), "_id", session_id, "clients", pagedAssembler, assembler, pageRequest);
    }

    /**
     * Method to get the Quotes for a specific Client
     * @param id Client ID
     * @param pageRequest
     * @param assembler
     * @return Quotes for a specific Client as a JSON array object.
     */
    @GetMapping(path="/quotes/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getQuotesForClient(  @PathVariable("id") String id,
                                                                          @RequestHeader String session_id,
                                                                          Pageable pageRequest,
                                                                          PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Quotes GET request for client with ID: "+ id);
        List<Quote> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("client_id").is(id)), Quote.class, "quotes");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/quotes")
    public ResponseEntity<Page<? extends MVGObject>> getQuotes(  @RequestHeader String session_id,
                                                                 Pageable pageRequest,
                                                                 PersistentEntityResourceAssembler assembler)
    {
        return getAll(new Notification(), session_id, "quotes", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping("/quote")
    public ResponseEntity<String> addQuote(@RequestHeader String session_id, @RequestBody Quote quote)
    {
        return put(quote, session_id, "quotes", "quotes_timestamp");
    }

    @PostMapping("/quote")
    public ResponseEntity<String> patchQuote(@RequestHeader String session_id, @RequestBody Quote quote)
    {
        return patch(quote, session_id, "quotes", "quotes_timestamp");
    }
}