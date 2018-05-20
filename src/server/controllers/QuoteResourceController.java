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
import server.model.QuoteItem;
import server.repositories.QuoteItemRepository;

import java.util.List;

@RepositoryRestController
public class QuoteResourceController extends APIController
{
    private PagedResourcesAssembler<QuoteItem> pagedAssembler;
    @Autowired
    private QuoteItemRepository quote_resourceRepository;

    @Autowired
    public QuoteResourceController(PagedResourcesAssembler<QuoteItem> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/quotes/resources/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getQuoteItem(@PathVariable("id") String id,
                                                                  @RequestHeader String session_id,
                                                                  Pageable pageRequest,
                                                                  PersistentEntityResourceAssembler assembler)
    {
        return get(new QuoteItem(id), "_id", session_id, "quote_resources", pagedAssembler, assembler, pageRequest);
    }

    @GetMapping("/quotes/resources")
    public ResponseEntity<Page<? extends MVGObject>> getQuoteItems(  Pageable pageRequest,
                                                                     @RequestHeader String session_id,
                                                                     PersistentEntityResourceAssembler assembler)
    {
        return getAll(new QuoteItem(), session_id, "quote_resources", pagedAssembler, assembler, pageRequest);
    }


    @PutMapping("/quotes/resources")
    public ResponseEntity<String> addQuoteResource(@RequestHeader String session_id, @RequestBody QuoteItem quote_item)
    {
        return put(quote_item, session_id, "quote_resources", "quote_resources_timestamp");
    }

    @PostMapping("/quotes/resources")
    public ResponseEntity<String> patchQuoteResource(@RequestHeader String session_id, @RequestBody QuoteItem quote_item)
    {
        return patch(quote_item, session_id, "quote_resources", "quote_resources_timestamp");
    }
}
