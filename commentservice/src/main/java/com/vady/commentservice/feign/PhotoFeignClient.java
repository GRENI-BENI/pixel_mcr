package com.vady.commentservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "photoservice", fallback = PhotoFallback.class)
public interface PhotoFeignClient {


    @GetMapping("/photos/ids/user/{keycloakId}")
    ResponseEntity<List<Long>> getPhotoIdsByUserKeycloakId(@PathVariable String keycloakId);

}
