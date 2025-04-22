package com.vady.iamservice.service;

import com.vady.iamservice.dto.LoginRequest;
import com.vady.iamservice.dto.RegistrationRequest;
import com.vady.iamservice.dto.TokenResponse;
import com.vady.iamservice.exception.AuthenticationException;
import com.vady.iamservice.exception.UserAlreadyExistsException;
import com.vady.iamservice.mapper.UserMapper;
import com.vady.iamservice.model.User;
import com.vady.iamservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void registerUser(RegistrationRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }
        
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserAlreadyExistsException("User with this nickname already exists");
        }

        // Create user in Keycloak
        keycloakService.createUser(request.getEmail(), request.getPassword(), 
                request.getNickname(), "");

        // Get Keycloak user ID
        String keycloakId = keycloakService.getUserIdByEmail(request.getEmail());
        if (keycloakId == null) {
            throw new RuntimeException("Failed to create user in Keycloak");
        }

        // Create user in our database
        User user = userMapper.toEntity(request);
        user.setKeycloakId(keycloakId);
        user.setEmail(request.getEmail());
        
        userRepository.save(user);
        log.info("User registered successfully with email: {}", request.getEmail());
    }

    public TokenResponse login(LoginRequest request) {
        try {
            TokenResponse tokenResponse = keycloakService.getToken(request.getEmail(), request.getPassword());
            log.info("User logged in successfully: {}", request.getEmail());
            return tokenResponse;
        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getEmail(), e);
            throw new AuthenticationException("Invalid credentials");
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        try {
            TokenResponse tokenResponse = keycloakService.refreshToken(refreshToken);
            log.info("Token refreshed successfully");
            return tokenResponse;
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new AuthenticationException("Invalid or expired refresh token");
        }
    }
}