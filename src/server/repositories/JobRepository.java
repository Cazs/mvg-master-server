package server.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.model.Job;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "jobs", path = "/jobs")
public interface JobRepository extends MongoRepository<Job, String>
{
    List<Job> findByCreator(@Param("creator") String creator);
}
