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
import server.model.Asset;
import server.repositories.AssetRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/assets")
public class AssetController
{
    private PagedResourcesAssembler<Asset> pagedAssembler;
    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    public AssetController(PagedResourcesAssembler<Asset> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<Asset>> getAsset(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Asset GET request id: "+ id);
        List<Asset> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Asset.class, "assets");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<Asset>> getAssets(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Asset GET request {all}");
        List<Asset> contents =  IO.getInstance().mongoOperations().findAll(Asset.class, "assets");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addAsset(@RequestBody Asset asset)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Asset creation request.");
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putBusinessObject(asset, "assets", "assets_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchAsset(@RequestBody Asset asset)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Asset update request.");
        return APIController.patchBusinessObject(asset, "assets", "assets_timestamp");
    }
}
