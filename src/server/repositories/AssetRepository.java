
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Asset;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "assets", path = "/assets")
public interface AssetRepository extends MongoRepository<Asset, String>
{
	List<Asset> findBy_id(@Param("_id") String _id);
}
