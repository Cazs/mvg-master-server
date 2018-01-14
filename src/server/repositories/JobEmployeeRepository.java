
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.JobEmployee;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "jobemployees", path = "/jobs/employees")
public interface JobEmployeeRepository extends MongoRepository<JobEmployee, String>
{
	List<JobEmployee> findBy_id(@Param("_id") String _id);
}
