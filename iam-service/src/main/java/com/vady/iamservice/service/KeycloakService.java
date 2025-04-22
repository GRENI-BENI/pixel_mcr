package com.vady.iamservice.service;

import com.vady.iamservice.config.KeycloakProperties;
import com.vady.iamservice.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final WebClient webClient;
    private final KeycloakProperties keycloakProperties;

    public void createUser(String email, String password, String firstName, String lastName) {
        String adminToken = getAdminToken();

        Map<String, Object> userRepresentation = Map.of(
                "username", email,
                "email", email,
                "enabled", true,
                "firstName", firstName != null ? firstName : "",
                "lastName", lastName != null ? lastName : "",
                "emailVerified", false,
                "credentials", new Object[]{
                        Map.of(
                                "type", "password",
                                "value", password,
                                "temporary", false
                        )
                }
        );

        webClient.post()
                .uri(keycloakProperties.getAdminUrl() + "/users")
                .headers(headers -> headers.setBearerAuth(adminToken))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRepresentation)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public String getUserIdByEmail(String email) {
        String adminToken = getAdminToken();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(keycloakProperties.getAdminUrl() + "/users")
                        .queryParam("email", email)
                        .queryParam("exact", true)
                        .build())
                .headers(headers -> headers.setBearerAuth(adminToken))
                .retrieve()
                .bodyToMono(Object[].class)
                .map(users -> {
                    if (users.length > 0) {
                        Map<String, Object> user = (Map<String, Object>) users[0];
                        return (String) user.get("id");
                    }
                    return null;
                })
                .block();
    }

    public TokenResponse getToken(String username, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", keycloakProperties.getClientId());
        formData.add("client_secret", keycloakProperties.getClientSecret());
        formData.add("grant_type", "password");
        formData.add("username", username);
        formData.add("password", password);

        return webClient.post()
                .uri(keycloakProperties.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }

    public TokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", keycloakProperties.getClientId());
        formData.add("client_secret", keycloakProperties.getClientSecret());
        formData.add("grant_type", "refresh_token");
        formData.add("refresh_token", refreshToken);

        return webClient.post()
                .uri(keycloakProperties.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }

    private String getAdminToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", keycloakProperties.getAdminClientId());
        formData.add("client_secret", keycloakProperties.getAdminClientSecret());
        formData.add("grant_type", "client_credentials");

        return webClient.post()
                .uri(keycloakProperties.getTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .block();
    }
}