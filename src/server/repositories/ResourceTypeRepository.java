
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.ResourceType;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "resource_types", path = "/resources/types")
public interface ResourceTypeRepository extends MongoRepository<ResourceType, String>
{
	List<ResourceType> findBy_id(@Param("_id") String _id);
}
