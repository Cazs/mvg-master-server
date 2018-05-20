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
import server.model.Metafile;
import server.repositories.MetafileRepository;

@RepositoryRestController
public class MetafileController extends APIController
{
    private PagedResourcesAssembler<Metafile> pagedAssembler;
    @Autowired
    private MetafileRepository metafileRepository;

    @Autowired
    public MetafileController(PagedResourcesAssembler<Metafile> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/file/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<? extends MVGObject>> getFile(@RequestHeader String session_id, @PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        return get(new Metafile(id), "_id", session_id, "metafiles", pagedAssembler, assembler, pageRequest);
    }

    @GetMapping("/files")
    public ResponseEntity<Page<? extends MVGObject>> getFiles(@RequestHeader String session_id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        return getAll(new Metafile(), session_id, "metafiles", pagedAssembler, assembler, pageRequest);
    }

    @PutMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestHeader String session_id, @RequestBody Metafile metafile)
    {
        return put(metafile, session_id, "metafiles", "metafiles_timestamp");
    }

    @PostMapping("/file")
    public ResponseEntity<String> patch(@RequestHeader String session_id, @RequestBody Metafile metafile)
    {
        return patch(metafile, session_id, "metafiles", "metafiles_timestamp");
    }
}
