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
import server.model.Expense;
import server.repositories.ExpenseRepository;

import java.util.List;

@RepositoryRestController
@RequestMapping("/expenses")
public class ExpenseController
{
        private PagedResourcesAssembler<Expense> pagedAssembler;
        @Autowired
        private ExpenseRepository expenseRepository;

        @Autowired
        public ExpenseController(PagedResourcesAssembler<Expense> pagedAssembler)
        {
            this.pagedAssembler = pagedAssembler;
        }

        @GetMapping(path="/{id}", produces = "application/hal+json")
        public ResponseEntity<Page<Expense>> getExpense(@PathVariable("id") String id, Pageable pageRequest, PersistentEntityResourceAssembler assembler)
        {
            IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Expense GET request id: "+ id);
            List<Expense> contents = IO.getInstance().mongoOperations().find(new Query(Criteria.where("_id").is(id)), Expense.class, "expenses");
            return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
        }

        @GetMapping
        public ResponseEntity<Page<Expense>> getExpenses(Pageable pageRequest, PersistentEntityResourceAssembler assembler)
        {
            IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Expense GET request {all}");
            List<Expense> contents =  IO.getInstance().mongoOperations().findAll(Expense.class, "expenses");
            return new ResponseEntity(pagedAssembler.toResource(new PageImpl(contents, pageRequest, contents.size()), (ResourceAssembler) assembler), HttpStatus.OK);
        }

        @PutMapping
        public ResponseEntity<String> addExpense(@RequestBody Expense expense)
        {
            IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Expense creation request.");
            //HttpHeaders headers = new HttpHeaders();
            return APIController.putBusinessObject(expense, "expenses", "expenses_timestamp");
        }

        @PostMapping
        public ResponseEntity<String> patchExpense(@RequestBody Expense expense)
        {
            IO.log(getClass().getName(), IO.TAG_INFO, "\nhandling Expense update request.");
            return APIController.patchBusinessObject(expense, "expenses", "expenses_timestamp");
        }
}
