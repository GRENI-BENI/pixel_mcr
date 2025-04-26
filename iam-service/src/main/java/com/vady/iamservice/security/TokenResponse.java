package com.vady.iamservice.security;
public record TokenResponse(String accessToken, String refreshToken, Long expiresIn) {}