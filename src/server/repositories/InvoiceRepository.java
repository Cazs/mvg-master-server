
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Invoice;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "invoices", path = "/invoices")
public interface InvoiceRepository extends MongoRepository<Invoice, String>
{
	List<Invoice> findBy_id(@Param("_id") String _id);
}
