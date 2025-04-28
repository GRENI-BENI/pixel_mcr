package com.vady.iamservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "photoservice", fallback = PhotoFallback.class)
public interface PhotoFeignClient {
    @GetMapping("/photos/count/user/{keycloakId}")
    ResponseEntity<Long> getPhotoCountByUserKeycloakId(@PathVariable String keycloakId);
}
