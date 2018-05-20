package server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.model.MVGObject;
import server.model.Resource;
import server.model.Supplier;
import server.repositories.SupplierRepository;

@RepositoryRestController
@RequestMapping("/suppliers")
public class SupplierController extends APIController
{
    private PagedResourcesAssembler<Supplier> pagedAssembler;
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    public SupplierController(PagedResourcesAssembler<Supplier> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getSupplier(@PathVariable("id") String id,
                                                                 @RequestHeader String session_id,
                                                                 Pageable pageRequest,
                                                                 PersistentEntityResourceAssembler assembler)
    {
        return get(new Supplier(id), "_id", session_id, "resources", pagedAssembler, assembler, pageRequest);
    }

    @GetMapping
    public ResponseEntity<Page<? extends MVGObject>> getSuppliers(Pageable pageRequest,
                                                       @RequestHeader String session_id,
                                                       PersistentEntityResourceAssembler assembler)
    {
        return getAll(new Resource(), session_id, "resources", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping
    public ResponseEntity<String> addSupplier(@RequestHeader String session_id, @RequestBody Supplier supplier)
    {
        return put(supplier, session_id, "suppliers", "suppliers_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchSupplier(@RequestHeader String session_id, @RequestBody Supplier supplier)
    {
        return patch(supplier, session_id, "suppliers", "suppliers_timestamp");
    }
}
