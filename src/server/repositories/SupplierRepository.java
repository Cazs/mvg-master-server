
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Supplier;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "suppliers", path = "/suppliers")
public interface SupplierRepository extends MongoRepository<Supplier, String>
{
	List<Supplier> findBy_id(@Param("_id") String _id);
}
