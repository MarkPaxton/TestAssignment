package nl.rabobank.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                        .csrf().disable()
                        .authorizeExchange((authorize) -> authorize
                                        .pathMatchers("/powerofattorney/**").authenticated()
                                        .pathMatchers("/**").permitAll()
                        )
                        .oauth2ResourceServer((resourceServer) -> resourceServer
                                        .jwt(withDefaults())
                        );
        return http.build();
    }

}