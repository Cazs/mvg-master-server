
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Client;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "clients", path = "/clients")
public interface ClientRepository extends MongoRepository<Client, String>
{
	List<Client> findBy_id(@Param("_id") String _id);
}
