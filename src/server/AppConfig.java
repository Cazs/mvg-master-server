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
import server.model.*;

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
        return (container -> container.setPort(8080));
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config)
    {
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(TripBooking.class);
        config.exposeIdsFor(Resource.class);
        config.exposeIdsFor(Client.class);
        config.exposeIdsFor(Message.class);
        config.exposeIdsFor(TripDriver.class);
        config.exposeIdsFor(AccommodationDestination.class);
        config.exposeIdsFor(AccommodationBooking.class);
    }
}
