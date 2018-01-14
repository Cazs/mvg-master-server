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
import server.exceptions.InvalidJobException;
import server.model.Counter;
import server.model.FileMetadata;
import server.model.Job;
import server.model.Quote;
import server.repositories.JobRepository;

import java.rmi.Remote;
import java.util.List;

@RepositoryRestController
//@RequestMapping("/jobs")
public class JobController
{
    private PagedResourcesAssembler<Job> pagedAssembler;
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    public JobController(PagedResourcesAssembler<Job> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/jobs/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Job>> getJob(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Job GET request id: "+ id);
        List<Job> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Job.class, "jobs");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping("/jobs")
    public ResponseEntity<Page<Job>> getJobs(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Job GET request {all}");
        List<Job> contents =  IO.getInstance().mongoOperations().findAll(Job.class, "jobs");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping("/jobs")
    public ResponseEntity<String> addJob(@RequestBody Job job)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Job creation request.");
        if(job!=null)
        {
            try
            {
                String new_job_id = RemoteComms.commitBusinessObjectToDatabase(job, "jobs", "jobs_timestamp");
                return new ResponseEntity<>(new_job_id, HttpStatus.OK);
            } catch (InvalidBusinessObjectException e)
            {
                IO.log(Remote.class.getName(),IO.TAG_ERROR, "invalid Job object: {"+e.getMessage()+"}");
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>("Invalid Job", HttpStatus.CONFLICT);
    }

    @PostMapping("/jobs")
    public ResponseEntity<String> patchJob(@RequestBody Job job)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Job update request.");
        return APIController.patchBusinessObject(job, "jobs", "jobs_timestamp");
    }

    @PostMapping(value = "/jobs/approval_request")//, consumes = "text/plain"//value =//, produces = "application/pdf"
    public ResponseEntity<String> requestJobApproval(@RequestHeader String job_id, @RequestHeader String session_id,
                                                       @RequestHeader String message, @RequestHeader String subject,
                                                       @RequestBody FileMetadata fileMetadata)//, @RequestParam("file") MultipartFile file
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Job approval request.");
        return APIController.requestBusinessObjectApproval(job_id, session_id, message, subject, fileMetadata, new Job().apiEndpoint(), Job.class);
    }

    @GetMapping("/jobs/approve/{job_id}/{vericode}")
    public ResponseEntity<String> approveJob(@PathVariable("job_id") String job_id, @PathVariable("vericode") String vericode)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Job "+job_id+" approval request by Vericode.");
        return APIController.approveBusinessObjectByVericode(job_id, vericode, "jobs", "jobs_timestamp", Job.class);
    }
}
