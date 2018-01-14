package server;

import com.mongodb.MongoClient;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import server.auxilary.RemoteComms;
import server.model.Enquiry;
import server.model.User;

import java.net.UnknownHostException;

@Configuration
public class AppConfig  extends RepositoryRestConfigurerAdapter
{
    @Bean
    public MongoDbFactory mongoDbFactory()
    {
        return new SimpleMongoDbFactory(new MongoClient(RemoteComms.DB_IP, RemoteComms.DB_PORT), RemoteComms.DB_NAME);
    }

    @Bean
    public MongoOperations mongoOperations() throws UnknownHostException
    {
        return new MongoTemplate(mongoDbFactory());
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer()
    {
        return (container -> {
            container.setPort(8083);
        });
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config)
    {
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Enquiry.class);
    }
    /*@Bean
    public Mongo mongo() throws Exception
    {
        return new Mongo("localhost");
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception
    {
        return new MongoTemplate(mongo(), "bms-server");
    }*/
}