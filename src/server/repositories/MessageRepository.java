
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Message;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "messages", path = "/messages")
public interface MessageRepository extends MongoRepository<Message, String>
{
	List<Message> findBy_id(@Param("_id") String _id);
}
