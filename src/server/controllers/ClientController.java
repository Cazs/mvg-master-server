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
import server.model.Client;

import java.util.List;

@RepositoryRestController
@RequestMapping("/clients")
public class ClientController
{
    private PagedResourcesAssembler<Client> pagedAssembler;

    @Autowired
    public ClientController(PagedResourcesAssembler<Client> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Client>> getClientById(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "handling GET request for Client: "+ id);
        List<Client> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Client.class, "clients");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Client>> getAllClients(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Client GET request {all}");
        List<Client> contents =  IO.getInstance().mongoOperations().findAll(Client.class, "clients");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addClient(@RequestBody Client client)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Client PUT request.");
        return APIController.putMVGObject(client, "clients", "clients_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchClient(@RequestBody Client client)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Client update request.");
        return APIController.patchMVGObject(client, "clients", "clients_timestamp");
    }
}
