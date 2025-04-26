package com.vady.iamservice.controller;


import com.vady.iamservice.dto.*;
import com.vady.iamservice.security.TokenResponse;
import com.vady.iamservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserProfileResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserProfileResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = userService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    public record CheckNicknameDto(boolean exist) {}
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNicknameExistence(@RequestParam String nickname) {
        return userService.existByNickname(nickname)? ResponseEntity.ok(new CheckNicknameDto(true)):ResponseEntity.ok(new CheckNicknameDto(false)) ;
    }

//    @GetMapping("/me")
//    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
//        String keycloakId = jwt.getSubject();
//        UserProfileResponse response = userService.getUserProfile(keycloakId);
//        return ResponseEntity.ok(response);
//    }
}