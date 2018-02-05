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
import server.model.Enquiry;
import server.model.FileMetadata;
import server.model.Enquiry;
import server.repositories.EnquiryRepository;
import server.repositories.EnquiryRepository;

import java.util.List;

@RepositoryRestController
public class EnquiryController
{
    private PagedResourcesAssembler<Enquiry> pagedAssembler;
    @Autowired
    private EnquiryRepository enquiryRepository;

    @Autowired
    public EnquiryController(PagedResourcesAssembler<Enquiry> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/enquiry/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Enquiry>> getEnquiry(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Enquiry GET request id: "+ id);
        List<Enquiry> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Enquiry.class, "enquiries");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    /**
     * Method to get the Enquiries for a specific Client
     * @param id Client ID
     * @param pageRequest
     * @param assembler
     * @return Enquiries for a specific Client as a JSON array object.
     */
    @GetMapping(path="/enquiries/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Enquiry>> getEnquiriesForClient(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Enquiries GET request for client with ID ["+id+"]");
        List<Enquiry> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("client_id").is(id)), Enquiry.class, "enquiries");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/enquiries")
    public ResponseEntity<Page<Enquiry>> getEnquirys(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Enquiry GET request {all}");
        List<Enquiry> contents =  IO.getInstance().mongoOperations().findAll(Enquiry.class, "enquiries");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    ////public ResponseEntity<Page<Enquiry>> addEnquiry(@RequestBody Enquiry enquiry, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    @PutMapping("/enquiries")
    public ResponseEntity<String> addEnquiry(@RequestBody Enquiry enquiry)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Enquiry creation request.");
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putMVGObject(enquiry, "enquiries", "enquiries_timestamp");
    }

    @PostMapping("/enquiries")
    public ResponseEntity<String> patchEnquiry(@RequestBody Enquiry enquiry)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Enquiry update request.");
        return APIController.patchMVGObject(enquiry, "enquiries", "enquiries_timestamp");
    }

    @PostMapping(value = "/enquiries/approval_request")//, consumes = "text/plain"//value =//, produces = "application/pdf"
    public ResponseEntity<String> requestEnquiryApproval(@RequestHeader String enquiry_id, @RequestHeader String session_id,
                                                       @RequestHeader String message, @RequestHeader String subject,
                                                       @RequestBody FileMetadata fileMetadata)//, @RequestParam("file") MultipartFile file
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Enquiry approval request.");
        return APIController.requestMVGObjectApproval(enquiry_id, session_id, message, subject, fileMetadata, new Enquiry().apiEndpoint(), Enquiry.class);
    }

    @GetMapping("/enquiries/approve/{enquiry_id}/{vericode}")
    public ResponseEntity<String> approveEnquiry(@PathVariable("enquiry_id") String enquiry_id, @PathVariable("vericode") String vericode)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Enquiry "+enquiry_id+" approval request by Vericode.");
        return APIController.approveMVGObjectByVericode(enquiry_id, vericode, "enquiries", "enquiries_timestamp", Enquiry.class);
    }
}
