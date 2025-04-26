package com.vady.photoservice.feign;

import com.vady.photoservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "iam-service", fallback = UserFallback.class, configuration = FeignClientConfig.class)
public interface UserFeignClient {
    @GetMapping("/user/me/keycloak")
    public ResponseEntity<UserDto> getCurrentUserByKeycloak(String id);

    @GetMapping("/user/nickname/{nickname}")
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable String nickname);
}
