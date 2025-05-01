package com.vady.iamservice.controller;

import com.vady.iamservice.dto.DonationRequest;
import com.vady.iamservice.dto.UserDto;
import com.vady.iamservice.dto.UserExtendedDto;
import com.vady.iamservice.dto.UserDonationDto; // Added import for UserDonationDto
import com.vady.iamservice.exception.ResourceNotFoundException;
import com.vady.iamservice.feign.PhotoFeignClient;
import com.vady.iamservice.mapper.UserExtendedMapper;
import com.vady.iamservice.mapper.UserMapper;
import com.vady.iamservice.model.User;
import com.vady.iamservice.service.UserService;
import com.vady.iamservice.service.UserDonationService; // Added import for UserDonationService
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final UserExtendedMapper userExtendedMapper;
    private final PhotoFeignClient photoFeignClient;
    private final UserDonationService userDonationService; // Added field for UserDonationService

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserPublicProfile(
            @PathVariable Long id,
            @RequestHeader(required = false,name = "X-User-ID") String userId) {
        User user = userService.getUserById(id);
        User currentUser=null;
       if(userId!= null) {
           currentUser=userService.getUserByKeycloak(userId);
       }

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
        UserDto userProfile = userService.getUserProfile(id);
        Long photosCount = photoFeignClient.getPhotoCountByUserKeycloakId(id).getBody();
        userProfile.setPhotosCount(photosCount != null ? photosCount.intValue() : 0);
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
    public ResponseEntity<?> followUser(
            @PathVariable String nickname,
            @RequestHeader(required = true,name = "X-User-ID") String userId) {
        User toFollow=userService.followUser(nickname, userService.getUserByKeycloak(userId));
        return getUserPublicProfile(toFollow.getId(),userId);
    }

    @DeleteMapping("/{nickname}/follow")
    public ResponseEntity<?> unfollowUser(
            @PathVariable String nickname,
            @RequestHeader(required = true,name = "X-User-ID") String userId) {
        User toUnFollow=userService.unfollowUser(nickname, userService.getUserByKeycloak(userId));
        return getUserPublicProfile(toUnFollow.getId(),userId);
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

    // Added endpoints for donations
    @GetMapping("/me/donations")
    public ResponseEntity<List<UserDonationDto>> getCurrentUserDonations(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userDonationService.getUserDonationsByKeycloakId(jwt.getSubject()));
    }

    @PostMapping("/me/donations")
    public ResponseEntity<List<UserDonationDto>> addUserDonation(
            @Valid @RequestBody DonationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            UserDonationDto donationDto = UserDonationDto.builder()
                    .platformId(request.getPlatformId())
                    .donationLink(request.getDonationLink())
                    .build();
            
            // Add the donation
            userDonationService.addUserDonation(jwt.getSubject(), donationDto);
            
            // Return the complete list of donations after adding
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(userDonationService.getUserDonationsByKeycloakId(jwt.getSubject()));
        } catch (ResponseStatusException e) {
            // Let the exception handler deal with it
            throw e;
        } catch (Exception e) {
            // Log the error and return a 400 Bad Request
            log.error("Error adding donation: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error adding donation: " + e.getMessage());
        }
    }

    @PutMapping("/me/donations/{donationId}")
    public ResponseEntity<List<UserDonationDto>> updateUserDonation(
            @PathVariable Long donationId,
            @Valid @RequestBody DonationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            UserDonationDto donationDto = UserDonationDto.builder()
                    .platformId(request.getPlatformId())
                    .donationLink(request.getDonationLink())
                    .build();
            
            // Update the donation
            userDonationService.updateUserDonation(jwt.getSubject(), donationId, donationDto);
            
            // Return the complete list of donations after updating
            return ResponseEntity.ok(userDonationService.getUserDonationsByKeycloakId(jwt.getSubject()));
        } catch (ResponseStatusException e) {
            // Let the exception handler deal with it
            throw e;
        } catch (Exception e) {
            // Log the error and return a 400 Bad Request
            log.error("Error updating donation: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating donation: " + e.getMessage());
        }
    }

    @DeleteMapping("/me/donations/{donationId}")
    public ResponseEntity<List<UserDonationDto>> deleteUserDonation(
            @PathVariable Long donationId,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            // Delete the donation
            userDonationService.deleteUserDonation(jwt.getSubject(), donationId);
            
            // Return the complete list of donations after deletion
            return ResponseEntity.ok(userDonationService.getUserDonationsByKeycloakId(jwt.getSubject()));
        } catch (ResponseStatusException e) {
            // Let the exception handler deal with it
            throw e;
        } catch (Exception e) {
            // Log the error and return a 400 Bad Request
            log.error("Error deleting donation: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error deleting donation: " + e.getMessage());
        }
    }

    // Added endpoint to get donations for a specific user
    @GetMapping("/{nickname}/donations")
    public ResponseEntity<List<UserDonationDto>> getUserDonations(@PathVariable String nickname) {
        User user = userService.getUserByNickname(nickname);
        return ResponseEntity.ok(userDonationService.getUserDonations(user.getId()));
    }

    /**
     * Get donations for a user by their ID
     * 
     * @param userId The ID of the user whose donations to retrieve
     * @return A list of donation DTOs for the specified user
     */
    @GetMapping("/{userId}/donations-by-id")
    public ResponseEntity<List<UserDonationDto>> getUserDonationsById(@PathVariable Long userId) {
        try {
            // Check if user exists
            userService.getUserById(userId); // This will throw ResourceNotFoundException if user doesn't exist
            
            // Get and return the user's donations
            return ResponseEntity.ok(userDonationService.getUserDonations(userId));
        } catch (ResourceNotFoundException e) {
            // Log the error and rethrow
            log.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            // Log any other errors and return a 500 Internal Server Error
            log.error("Error retrieving donations for user ID {}: {}", userId, e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving donations: " + e.getMessage()
            );
        }
    }
}