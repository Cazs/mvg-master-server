
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Enquiry;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "enquiries", path = "/enquiries")
public interface EnquiryRepository extends MongoRepository<Enquiry, String>
{
	List<Enquiry> findBy_id(@Param("_id") String _id);
}
