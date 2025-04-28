package com.vady.iamservice.feign;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PhotoFallback implements PhotoFeignClient {
    @Override
    public ResponseEntity<Long> getPhotoCountByUserKeycloakId(String keycloakId) {
        return ResponseEntity.ok(0L);
    }
}