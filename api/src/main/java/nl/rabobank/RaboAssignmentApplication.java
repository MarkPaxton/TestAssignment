package nl.rabobank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;

import nl.rabobank.mongo.MongoConfiguration;

@SpringBootApplication
@Import(MongoConfiguration.class)
public class RaboAssignmentApplication {
    public static void main(final String[] args) {
        SpringApplication.run(RaboAssignmentApplication.class, args);
    }
}
