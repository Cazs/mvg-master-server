
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Revenue;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "revenues", path = "/revenues")
public interface RevenueRepository extends MongoRepository<Revenue, String>
{
	List<Revenue> findBy_id(@Param("_id") String _id);
}
