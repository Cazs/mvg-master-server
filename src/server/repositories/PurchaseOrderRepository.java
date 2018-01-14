
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.PurchaseOrder;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "purchase_orders", path = "/purchaseorders")
public interface PurchaseOrderRepository extends MongoRepository<PurchaseOrder, String>
{
	List<PurchaseOrder> findBy_id(@Param("_id") String _id);
}
