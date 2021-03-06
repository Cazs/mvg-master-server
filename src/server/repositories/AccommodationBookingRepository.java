package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.AccommodationBooking;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "accommodation_bookings", path = "/bookings/accommodation")
public interface AccommodationBookingRepository extends MongoRepository<AccommodationBooking, String>
{
    List<AccommodationBooking> findByCreator(@Param("creator") String creator);
}
