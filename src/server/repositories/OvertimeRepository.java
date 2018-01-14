
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Overtime;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "overtime_applications", path = "/overtime_records")
public interface OvertimeRepository extends MongoRepository<Overtime, String>
{
	List<Overtime> findBy_id(@Param("_id") String _id);
}
