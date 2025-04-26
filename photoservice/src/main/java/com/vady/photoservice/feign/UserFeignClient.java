package com.vady.photoservice.feign;

import com.vady.photoservice.dto.KeycloakIdResponse;
import com.vady.photoservice.dto.UserDto;
import com.vady.photoservice.dto.UserExtendedDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@FeignClient(name = "iam-service", fallback = UserFallback.class, configuration = FeignClientConfig.class)
public interface UserFeignClient {
    @GetMapping("/user/me/keycloak")
    public ResponseEntity<UserDto> getCurrentUserByKeycloak(@RequestParam String id);

    @GetMapping("/user/nickname/{nickname}")
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable String nickname);

    @GetMapping("/user/keycloak/{nickname}")
    public ResponseEntity<KeycloakIdResponse> getUserKeycloakByNickname(@PathVariable String nickname);

    @GetMapping("/user/batch-get")
    ResponseEntity<List<UserExtendedDto>> getUsersByIds(@RequestParam Set<String> userIds);

    public record UpdateProfileImageRequest(String imageUrl) {}

    @PutMapping("/user/me/profile-image")
    public ResponseEntity<UserDto> updateProfileImage(@RequestBody UpdateProfileImageRequest request,@RequestHeader("X-User-ID") String userId);

}
