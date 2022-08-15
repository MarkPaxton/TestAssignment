package nl.rabobank.mongo;

import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableMongoRepositories
@EnableMongoAuditing
@EnableConfigurationProperties(MongoProperties.class)
@Import(EmbeddedMongoAutoConfiguration.class)
@RequiredArgsConstructor
@EnableReactiveMongoRepositories

public class MongoConfiguration extends AbstractMongoClientConfiguration
{
    private final MongoProperties mongoProperties;

    @Override
    protected String getDatabaseName()
    {
        return mongoProperties.getMongoClientDatabase();
    }

    @Override
    @Bean(destroyMethod = "close")
    public MongoClient mongoClient()
    {
        return MongoClients.create(mongoProperties.determineUri());
    }
}
