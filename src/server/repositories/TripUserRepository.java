
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.TripDriver;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "tripusers", path = "/trips/users")
public interface TripUserRepository extends MongoRepository<TripDriver, String>
{
	List<TripDriver> findBy_id(@Param("_id") String _id);
}
