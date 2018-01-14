
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.QuoteRep;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "quote_representatives", path = "/quotes/representatives")
public interface QuoteRepRepository extends MongoRepository<QuoteRep, String>
{
	List<QuoteRep> findBy_id(@Param("_id") String _id);
}
