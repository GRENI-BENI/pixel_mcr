package com.vady.iamservice.service;


import com.vady.iamservice.dto.LoginRequest;
import com.vady.iamservice.dto.RefreshTokenRequest;
import com.vady.iamservice.dto.UserRegistrationRequest;
import com.vady.iamservice.exception.AuthenticationException;
import com.vady.iamservice.exception.KeycloakIntegrationException;
import com.vady.iamservice.security.KeycloakProperties;
import com.vady.iamservice.security.TokenResponse;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

    private final Keycloak keycloakAdminClient;
    private final WebClient webClient;
    private final KeycloakProperties keycloakProperties;

    /**
     * Creates a new user in Keycloak
     * @param request User registration data
     * @return Keycloak user ID
     */
    public String createKeycloakUser(UserRegistrationRequest request) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();

            // Create user representation
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(request.getEmail());
            user.setEmail(request.getEmail());
            user.setEmailVerified(true);

            // Set additional attributes if needed
//            Map<String, List<String>> attributes = new HashMap<>();
//            attributes.put("nickname", Collections.singletonList(request.nickname()));
//            user.setAttributes(attributes);

            // Create user

            Response response = usersResource.create(user);

            if (response.getStatus() < 200 || response.getStatus() >= 300) {
                throw new KeycloakIntegrationException("Failed to create user in Keycloak: " + response.getStatusInfo().getReasonPhrase());
            }

            // Extract user ID from response
            String userId = extractCreatedId(response);

            // Set password
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(request.getPassword());

            usersResource.get(userId).resetPassword(passwordCred);

            return userId;
        } catch (Exception e) {
            log.error("Error creating user in Keycloak", e);
            throw new KeycloakIntegrationException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the created user ID from the Keycloak response
     */
    private String extractCreatedId(Response response) {
        String locationHeader = response.getHeaderString("Location");
        if (locationHeader == null) {
            throw new KeycloakIntegrationException("Location header not found in Keycloak response");
        }

        String[] parts = locationHeader.split("/");
        return parts[parts.length - 1];
    }
/**
 * Authenticates a user with Keycloak using the official Keycloak client
 */
public TokenResponse login(LoginRequest request) {
    try {
        log.info("Attempting to authenticate user: {}", request.getEmail());

        // Create a new Keycloak instance for this authentication
        AccessTokenResponse tokenResponse;
        try (Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getAuthServerUrl())
                .realm(keycloakProperties.getRealm())
                .clientId(keycloakProperties.getResource())
                .clientSecret(keycloakProperties.getCredentials().getSecret())
                .username(request.getEmail())
                .password(request.getPassword())
                .grantType(OAuth2Constants.PASSWORD)
                .build()) {

            // This will throw an exception if authentication fails
            tokenResponse = keycloak.tokenManager().getAccessToken();
        }

        // Convert to your TokenResponse format
        TokenResponse response = new TokenResponse(tokenResponse.getToken(),tokenResponse.getRefreshToken(),tokenResponse.getExpiresIn());

        log.info("User authenticated successfully: {}", request.getEmail());
        return response;
    } catch (Exception e) {
        log.error("Error during login", e);
        throw new AuthenticationException("Invalid credentials");
    }
}
    


    /**
     * Gets a user ID from Keycloak by email
     * @param email User's email
     * @return Keycloak user ID
     */
    public String getUserIdByEmail(String email) {
        try {
            RealmResource realmResource = keycloakAdminClient.realm(keycloakProperties.getRealm());
            UsersResource usersResource = realmResource.users();

            List<UserRepresentation> users = usersResource.searchByEmail(email, true);

            if (users.isEmpty()) {
                log.warn("No user found with email: {}", email);
                return null;
            }

            if (users.size() > 1) {
                log.warn("Multiple users found with email: {}", email);
            }

            return users.get(0).getId();
        } catch (Exception e) {
            log.error("Error getting user ID from Keycloak by email: {}", email, e);
            throw new KeycloakIntegrationException("Failed to get user ID from Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Refreshes an access token using a refresh token
     */
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("client_id", keycloakProperties.getResource());
            formData.add("client_secret", keycloakProperties.getCredentials().getSecret());
            formData.add("refresh_token", request.getRefreshToken());

            return webClient.post()
                    .uri(keycloakProperties.getAuthServerUrl() + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError,
                            response -> Mono.error(new AuthenticationException("Invalid or expired refresh token")))
                    .onStatus(HttpStatusCode::is5xxServerError,
                            response -> Mono.error(new KeycloakIntegrationException("Keycloak server error")))
                    .bodyToMono(TokenResponse.class)
                    .block();
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new KeycloakIntegrationException("Failed to refresh token: " + e.getMessage(), e);
        }
    }
}