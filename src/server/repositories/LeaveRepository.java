
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Leave;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "leave_applications", path = "/leave_records")
public interface LeaveRepository extends MongoRepository<Leave, String>
{
	List<Leave> findBy_id(@Param("_id") String _id);
}
