
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Notification;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "notifications", path = "/notifications")
public interface NotificationRepository extends MongoRepository<Notification, String>
{
	List<Notification> findBy_id(@Param("_id") String _id);
}
