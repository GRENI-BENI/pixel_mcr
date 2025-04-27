package com.vady.iamservice.service;


import com.vady.iamservice.dto.*;
import com.vady.iamservice.exception.ResourceAlreadyExistsException;
import com.vady.iamservice.exception.ResourceNotFoundException;
import com.vady.iamservice.mapper.UserMapper;
import com.vady.iamservice.model.User;
import com.vady.iamservice.repository.UserRepository;
import com.vady.iamservice.security.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    @Value("${app.default-user-icon}")
    String defaultUserIcon;

    /**
     * Registers a new user in both Keycloak and the application database
     */
    @Transactional
    public UserProfileResponse registerUser(UserRegistrationRequest request) {
        // Check if email or nickname already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ResourceAlreadyExistsException("Nickname already taken");
        }

        // Create user in Keycloak
        String keycloakId = keycloakService.createKeycloakUser(request);
        if (request.getProfileImage()==null) {
            request.setProfileImage(defaultUserIcon);
        }
        // Create user in our database
        User user=new User(keycloakId,request.getNickname(),request.getEmail(),true,"Nothing tet/",request.getProfileImage());

        User savedUser = userRepository.save(user);

        return mapToUserProfileResponse(savedUser);
    }

    public boolean existByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * Authenticates a user and returns tokens
     */
    public TokenResponse loginUser(LoginRequest request) {
        return keycloakService.login(request);
    }

    /**
     * Refreshes an access token
     */
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        return keycloakService.refreshToken(request);
    }

    /**
     * Gets a user profile by Keycloak ID
     */
    public UserDto getUserProfile(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User","Keycloak ID", keycloakId));

        return userMapper.toDto(user);
    }

    /**
     * Maps a User entity to UserProfileResponse DTO
     */
    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .keycloakId(user.getKeycloakId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User","id",String.valueOf(id)));
    }

    public User updateUserAbout(String keycloakId, String about) {
        User user = getUserByKeycloak(keycloakId);
        user.setAbout(about);
        return userRepository.save(user);
    }
@Transactional
    public User updateUser(Long id, @Valid UserDto userDto) {
        User user = getUserById(id);

        if (userDto.getNickname() != null) {
            user.setNickname(userDto.getNickname());
        }

        if (userDto.getAbout() != null) {
            user.setAbout(userDto.getAbout());
        }

        return userRepository.save(user);
    }


    public void followUser(String nickname, User currentUser) {
        User userToFollow = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("User", "nickname", nickname));

        currentUser.getFollowing().add(userToFollow);
        userRepository.save(currentUser);
    }

    public void unfollowUser(String nickname, User currentUser) {
        User userToUnfollow = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("User", "nickname", nickname));

        currentUser.getFollowing().remove(userToUnfollow);
        userRepository.save(currentUser);
    }

    public Page<User> getUserFollowers(String nickname, Pageable pageable) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new ResourceNotFoundException("User", "nickname", nickname));

        // Convert Set to List for sorting
        List<User> sortedFollowers = new ArrayList<>(user.getFollowers());

        // Sort by follower count (descending)
        sortedFollowers.sort(Comparator.comparing((User u) -> u.getFollowers().size()).reversed());

        // Convert to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedFollowers.size());

        return new PageImpl<>(
                sortedFollowers.subList(start, end),
                pageable,
                sortedFollowers.size()
        );
    }

    public Page<User> getUserFollowing(String username, Pageable pageable) {
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "nickname", username));

        // Convert Set to List for sorting
        List<User> sortedFollowing = new ArrayList<>(user.getFollowing());

        // Sort by follower count (descending)
        sortedFollowing.sort(Comparator.comparing((User u) -> u.getFollowers().size()).reversed());

        // Convert to Page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sortedFollowing.size());

        return new PageImpl<>(
                sortedFollowing.subList(start, end),
                pageable,
                sortedFollowing.size()
        );
    }

    public User getUser(Jwt jwt){
        String keycloakId = jwt.getSubject();
        return userRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new ResourceNotFoundException("User", "Keycloak ID", keycloakId));
    }

    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
               .orElseThrow(() -> new ResourceNotFoundException("User", "nickname", nickname));
    }

    public User getUserByKeycloak(String id) {
        return userRepository.findByKeycloakId(id)
               .orElseThrow(() -> new ResourceNotFoundException("User", "Keycloak ID", id));
    }

    public String getUserKeycloakIdByNickname(String nickname) {
       return  userRepository.findKeycloakIdByNickname(nickname).orElseThrow(() -> new ResourceNotFoundException("User", "nickname", nickname));
    }

    @Transactional
    public User updateProfileImage(String keycloakId, String imageUrl) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Keycloak ID", keycloakId));

        user.setProfileImage(imageUrl);
        return userRepository.save(user);
    }

    public List<User> getUsersByIds(Set<String> userIds) {
        log.info("Getting users by IDs: {}", userIds);
        List<User> users= userRepository.findAllByKeycloakId(userIds);
        log.info("Fetched users: {}", users);
        return users;
    }
}