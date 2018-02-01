package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Trip;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "trips", path = "/trips")
public interface TripRepository extends MongoRepository<Trip, String>
{
    List<Trip> findByCreator(@Param("creator") String creator);
}
