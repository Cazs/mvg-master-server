
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.PurchaseOrderAsset;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "purchase_order_assets", path = "/purchaseorders/assets")
public interface PurchaseOrderAssetRepository extends MongoRepository<PurchaseOrderAsset, String>
{
	List<PurchaseOrderAsset> findBy_id(@Param("_id") String _id);
}
