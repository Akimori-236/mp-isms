package tfip.akimori.server.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String MONGO_URI;
    @Value("${spring.data.mongodb.database}")
    private String DB_NAME;

    @Bean
    public MongoTemplate createMongoTemplate() {
        // create client
        MongoClient client = MongoClients.create(MONGO_URI);
        // return template with client and database (must be correct)
        return new MongoTemplate(client, DB_NAME);
    }

}
