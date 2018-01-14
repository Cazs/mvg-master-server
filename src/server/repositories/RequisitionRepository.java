
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Requisition;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "requisitions", path = "/requisitions")
public interface RequisitionRepository extends MongoRepository<Requisition, String>
{
	List<Requisition> findBy_id(@Param("_id") String _id);
}
