package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.AccommodationDestination;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "accommodation_destinations", path = "/destinations")
public interface AccommodationDestinationRepository extends MongoRepository<AccommodationDestination, String>
{
	List<AccommodationDestination> findBy_id(@Param("_id") String _id);
}
