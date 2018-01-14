
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.User;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "users", path = "/users")
public interface UserRepository extends MongoRepository<User, String>
{
	List<User> findByLastname(@Param("lastname") String lastname);
}
