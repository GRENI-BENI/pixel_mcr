package com.vady.photoservice.feign;


import com.vady.photoservice.dto.KeycloakIdResponse;
import com.vady.photoservice.dto.UserDto;
import com.vady.photoservice.dto.UserExtendedDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Component
public class UserFallback implements UserFeignClient {
    @Override
    public ResponseEntity<UserDto> getCurrentUserByKeycloak(String id) {return null; }

    @Override
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable String nickname){return null;}

    @Override
    public ResponseEntity<KeycloakIdResponse> getUserKeycloakByNickname(@PathVariable String nickname){
        return null;
    }

    @Override
    public ResponseEntity<List<UserExtendedDto>> getUsersByIds(Set<String> userIds) {
        return null;
    }

    @Override
    public ResponseEntity<UserDto> updateProfileImage(@RequestBody UpdateProfileImageRequest request, @RequestHeader("X-User-ID") String userId){return null;}

}
