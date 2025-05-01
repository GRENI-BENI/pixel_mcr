package com.vady.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

//    @Bean
//    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
//        serverHttpSecurity.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated()
//                     .pathMatchers(HttpMethod.GET,"/api/comments/**").permitAll()
//                     .pathMatchers("/api/iam/auth/**").permitAll()
//
//                )
//                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(Customizer.withDefaults()));
//        serverHttpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable);
//        serverHttpSecurity.cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()));
//
//        return serverHttpSecurity.build();
//    }
@Bean
public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
    serverHttpSecurity.authorizeExchange(exchanges -> exchanges
                    // Auth endpoints - public access
                    .pathMatchers("/api/iam/auth/**").permitAll()

                    // Comment endpoints - GET is public, POST requires authentication
                    .pathMatchers(HttpMethod.GET, "/api/comments/**").permitAll()

                    // User endpoints - some public, some protected
                    .pathMatchers(HttpMethod.GET, "/api/iam/user/nickname/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/iam/user/keycloak/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/iam/user/*/followers", "/api/iam/user/*/following").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/iam/user/batch-get").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/iam/user/{id}").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/iam/platforms/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/iam/user/{userId}/donations-by-id").permitAll()
                    // Photo endpoints - GET is mostly public
                    .pathMatchers(HttpMethod.GET, "/api/photos/**").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/photos/trending").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/photos/tags").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/photos/user/**").permitAll()

                    // All other requests require authentication
                    .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oAuth2ResourceServerSpec ->
                    oAuth2ResourceServerSpec.jwt(Customizer.withDefaults())
            )
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()));

    return serverHttpSecurity.build();
}

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}