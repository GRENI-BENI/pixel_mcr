package com.vady.photoservice.feign;


import com.vady.photoservice.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
public class UserFallback implements UserFeignClient {
    @GetMapping("/me/keycloak")
    public ResponseEntity<UserDto> getCurrentUserByKeycloak(String id) {return null; }

    @Override
    public ResponseEntity<UserDto> getUserByNickname(@PathVariable String nickname){return null;};
}
