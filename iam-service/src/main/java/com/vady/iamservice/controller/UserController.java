package com.vady.iamservice.controller;

import com.vady.iamservice.dto.UserDto;
import com.vady.iamservice.dto.UserExtendedDto;
import com.vady.iamservice.mapper.UserExtendedMapper;
import com.vady.iamservice.mapper.UserMapper;
import com.vady.iamservice.model.User;
import com.vady.iamservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserExtendedMapper userExtendedMapper;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserPublicProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toPublicDto(user, currentUser));
    }

    public record ChangeBioRequest(String about) {}

    @PutMapping("/me/about")
    public ResponseEntity<UserDto> updateUserAbout(
            @RequestBody  ChangeBioRequest about,
            @AuthenticationPrincipal Jwt jwt) {
        User updatedUser = userService.updateUserAbout(jwt.getSubject(), about.about);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@RequestHeader("X-User-ID") String id) {
        log.error(id);
        UserDto userProfile = userService.getUserProfile(id);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateCurrentUser(
            @RequestBody @Valid UserDto userDto,
            @AuthenticationPrincipal Jwt jwt) {
        User updatedUser = userService.updateUser(userService.getUser(jwt).getId(), userDto);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    //

    @PostMapping("/{nickname}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable String nickname,
            @AuthenticationPrincipal Jwt jwt) {
        userService.followUser(nickname, userService.getUser(jwt));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{nickname}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable String nickname,
            @AuthenticationPrincipal Jwt jwt) {
        userService.unfollowUser(nickname, userService.getUser(jwt));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{nickname}/followers")
    public ResponseEntity<Page<UserDto>> getUserFollowers(
            @PathVariable String nickname,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> followers = userService.getUserFollowers(nickname, pageable);
        return ResponseEntity.ok(followers.map(userMapper::toDto));
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<Page<UserDto>> getUserFollowing(
            @PathVariable String username,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> following = userService.getUserFollowing(username, pageable);
        return ResponseEntity.ok(following.map(userMapper::toDto));
    }
    
        @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UserDto> getUserByNickname(
            @PathVariable String nickname) {
        User user = userService.getUserByNickname(nickname);
        return ResponseEntity.ok(userMapper.toPublicDto(user));
    }

    public record KeycloakIdResponse(String keycloakId) {}

    @GetMapping("/keycloak/{nickname}")
    public ResponseEntity<KeycloakIdResponse> getUserKeycloakByNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(new KeycloakIdResponse( userService.getUserKeycloakIdByNickname(nickname)));
    }

    @GetMapping("/me/keycloak")
    public ResponseEntity<UserDto> getCurrentUserByKeycloak(@RequestParam String id) {
        return ResponseEntity.ok(userMapper.toDto( userService.getUserByKeycloak(id)));
    }
    
    public record UpdateProfileImageRequest(String imageUrl) {}

    @PutMapping("/me/profile-image")
    public ResponseEntity<UserDto> updateProfileImage(
            @RequestBody UpdateProfileImageRequest request,
            @RequestHeader("X-User-ID") String userId) {
        User updatedUser = userService.updateProfileImage(userId, request.imageUrl());
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    @GetMapping("/batch-get")
    ResponseEntity<List<UserExtendedDto>> getUsersByIds(@RequestParam Set<String> userIds){
        return ResponseEntity.ok(userService.getUsersByIds(userIds).stream().map(userExtendedMapper::toDto).toList());
    }

}
