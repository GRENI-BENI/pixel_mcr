package com.vady.iamservice.controller;


import com.vady.iamservice.service.AuthService;
import com.vady.iamservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public record LoginRequest(String email, String password) {}
    public record RefreshTokenRequest(String refreshToken) {}
    public record RegistrationRequest(String email,String password, String nickname, String about, String profileImage) {}
    public record TokenResponse(String accessToken, String refreshToken, int expiresIn) {}

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegistrationRequest request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        UserProfileResponse userProfile = userService.getUserProfile(keycloakId);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenResponse tokenResponse = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }
}