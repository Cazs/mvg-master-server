
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Metafile;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "metafiles", path = "/file")
public interface MetafileRepository extends MongoRepository<Metafile, String>
{
	List<Metafile> findBy_id(@Param("_id") String _id);
}
