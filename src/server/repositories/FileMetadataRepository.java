
package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.FileMetadata;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "file_metadata", path = "/file/metadata")
public interface FileMetadataRepository extends MongoRepository<FileMetadata, String>
{
	List<FileMetadata> findBy_id(@Param("_id") String _id);
}
