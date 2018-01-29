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
import server.model.AssetType;
import server.repositories.AssetTypeRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/assets/types")
public class AssetTypeController
{
    private PagedResourcesAssembler<AssetType> pagedAssembler;
    @Autowired
    private AssetTypeRepository asset_typeRepository;

    @Autowired
    public AssetTypeController(PagedResourcesAssembler<AssetType> pagedAssembler)
    {
        this.pagedAssembler = pagedAssembler;
    }

    @GetMapping(path="/{id}", produces = "application/hal+json")
    public ResponseEntity<Page<AssetType>> getAssetType(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling AssetType GET request id: "+ id);
        List<AssetType> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), AssetType.class, "asset_types");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<AssetType>> getAssetTypes(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling AssetType GET request {all}");
        List<AssetType> contents =  IO.getInstance().mongoOperations().findAll(AssetType.class, "asset_types");
        return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> addAssetType(@RequestBody AssetType asset_type)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling AssetType creation request.");
        //HttpHeaders headers = new HttpHeaders();
        return APIController.putMVGObject(asset_type, "asset_types", "assets_timestamp");
    }

    @PostMapping
    public ResponseEntity<String> patchAssetType(@RequestBody AssetType asset_type)
    {
        IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling AssetType update request.");
        return APIController.patchMVGObject(asset_type, "asset_types", "assets_timestamp");
    }
}
