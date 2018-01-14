
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.QuoteItem;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "quote_resources", path = "/quotes/resources")
public interface QuoteItemRepository extends MongoRepository<QuoteItem, String>
{
	List<QuoteItem> findBy_id(@Param("_id") String _id);
}
