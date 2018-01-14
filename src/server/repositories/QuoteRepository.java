
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Quote;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "quotes", path = "/quotes")
public interface QuoteRepository extends MongoRepository<Quote, String>
{
	List<Quote> findBy_id(@Param("_id") String _id);
}
