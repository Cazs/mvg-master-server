
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.PurchaseOrderResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "purchase_order_resources", path = "/purchaseorders/resources")
public interface PurchaseOrderResourceRepository extends MongoRepository<PurchaseOrderResource, String>
{
	List<PurchaseOrderResource> findBy_id(@Param("_id") String _id);
}
