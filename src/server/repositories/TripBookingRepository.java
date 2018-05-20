package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.TripBooking;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "trip_bookings", path = "/bookings/trips")
public interface TripBookingRepository extends MongoRepository<TripBooking, String>
{
    List<TripBooking> findByCreator(@Param("creator") String creator);
}
