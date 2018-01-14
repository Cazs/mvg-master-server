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
//@RequestMapping("/quotes")
public class QuoteController
{
    private PagedResourcesAssembler<Quote> pagedAssembler;
    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    public QuoteController(PagedResourcesAssembler<Quote> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/quotes/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Quote>> getQuote(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Quote GET request id: "+ id);
        List<Quote> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Quote.class, "quotes");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/quotes")
    public ResponseEntity<Page<Quote>> getQuotes(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Quote GET request {all}");
        List<Quote> contents =  IO.getInstance().mongoOperations().findAll(Quote.class, "quotes");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    ////public ResponseEntity<Page<Quote>> addQuote(@RequestBody Quote quote, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    @PutMapping("/quotes")
    public ResponseEntity<String> addQuote(@RequestBody Quote quote)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Quote creation request.");
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putBusinessObject(quote, "quotes", "quotes_timestamp");
    }

    @PostMapping("/quotes")
    public ResponseEntity<String> patchQuote(@RequestBody Quote quote)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Quote update request.");
        return APIController.patchBusinessObject(quote, "quotes", "quotes_timestamp");
    }

    @PostMapping(value = "/quotes/approval_request")//, consumes = "text/plain"//value =//, produces = "application/pdf"
    public ResponseEntity<String> requestQuoteApproval(@RequestHeader String quote_id, @RequestHeader String session_id,
                                                       @RequestHeader String message, @RequestHeader String subject,
                                                       @RequestBody FileMetadata fileMetadata)//, @RequestParam("file") MultipartFile file
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Quote approval request.");
        return APIController.requestBusinessObjectApproval(quote_id, session_id, message, subject, fileMetadata, new Quote().apiEndpoint(), Quote.class);
    }

    @GetMapping("/quotes/approve/{quote_id}/{vericode}")
    public ResponseEntity<String> approveQuote(@PathVariable("quote_id") String quote_id, @PathVariable("vericode") String vericode)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Quote "+quote_id+" approval request by Vericode.");
        return APIController.approveBusinessObjectByVericode(quote_id, vericode, "quotes", "quotes_timestamp", Quote.class);
    }
}
