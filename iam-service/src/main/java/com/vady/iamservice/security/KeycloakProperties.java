package com.vady.iamservice.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private String authServerUrl;
    private String realm;
    private String resource;

    private Credentials credentials = new Credentials();
    private Admin admin = new Admin();

    @Data
    public static class Credentials {
        private String secret;
    }

    @Data
    public static class Admin {
        private String password;
        private String clientId;
        private String clientSecret;
        private String username;
    }
}
