package nl.rabobank;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@EnableWebFlux
@Configuration
public class WebConfig {
    @Bean
    public RouterFunction<ServerResponse> publicRouter() {
        return RouterFunctions
                        .resources("/public/**", new ClassPathResource("public/"));
    }
}
