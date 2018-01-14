
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Expense;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "expenses", path = "/expenses")
public interface ExpenseRepository extends MongoRepository<Expense, String>
{
	List<Expense> findBy_id(@Param("_id") String _id);
}
