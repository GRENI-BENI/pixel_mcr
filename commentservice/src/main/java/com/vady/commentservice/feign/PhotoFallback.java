package com.vady.commentservice.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PhotoFallback implements PhotoFeignClient {


    @Override
    public ResponseEntity<List<Long>> getPhotoIdsByUserKeycloakId(String keycloakId) {
        log.error("Failed to get photo IDs for user with keycloak ID: {}", keycloakId);
        return ResponseEntity.ok(Collections.emptyList());
    }
}