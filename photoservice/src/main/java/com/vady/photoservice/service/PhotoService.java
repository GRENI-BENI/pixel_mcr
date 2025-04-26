package com.vady.photoservice.service;

import com.vady.photoservice.controller.PhotoController;
import com.vady.photoservice.dto.*;
import com.vady.photoservice.exception.AuthenticationException;
import com.vady.photoservice.exception.ResourceNotFoundException;
import com.vady.photoservice.feign.UserFeignClient;
import com.vady.photoservice.model.Photo;
import com.vady.photoservice.model.Tag;
import com.vady.photoservice.repository.PhotoRepository;
import com.vady.photoservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {
    
    private final PhotoRepository photoRepository;
    private final TagRepository tagRepository;
    private final S3Service s3Service;
    private final UserFeignClient userFeignClient;

//    public Page<Photo> getAllPhotos(Pageable pageable) {
//        return photoRepository.findAllByOrderByCreatedAtDesc(pageable);
//    }
//
//    public Page<Photo> getPhotosByUser(Long userId, Pageable pageable) {
//        return photoRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
//    }
    
 public Page<PhotoCardDto> getPhotosByTag(Set<String> tagNames, String currentUserId, Pageable pageable) {
     Page<PhotoCardProjection> projections;

     if (tagNames.size() == 1) {
         // If only one tag, use the existing repository method
         String tagName = tagNames.iterator().next();
         projections = photoRepository.findPhotoCardsByTagName(tagName, currentUserId, pageable);
     } else {
         // For multiple tags
         projections = photoRepository.findPhotoCardsByTagNames(tagNames, currentUserId, pageable);
     }

     // Create a set of all unique userIds from the projections to batch fetch user data
     Set<String> userIds = projections.getContent().stream()
             .map(PhotoCardProjection::getUserId)
             .collect(Collectors.toSet());

     // Batch fetch user information from IAM service
     Map<String, UserExtendedDto> userInfoMap = fetchUserInfoBatch(userIds);

     // Map projections to DTOs with user information
     return projections.map(projection -> {
         UserExtendedDto userInfo = userInfoMap.get(projection.getUserId());

         return PhotoCardDto.builder()
                 .id(projection.getId())
                 .url(projection.getUrl())
                 .nickname(userInfo.getNickname())
                 .userProfileImage(userInfo.getProfileImage())
                 .isLiked(projection.getIsLiked())
                 .likesCount(projection.getLikesCount())
                 .build();
     });
}
    
    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo ","id",String.valueOf(id)));
    }

    public Photo getById(Long id) {
        return photoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Photo ","id",String.valueOf(id)));
    }




    public Page<Photo> getPhotosByUserKeycloakId(String s, Pageable pageable) {
        return photoRepository.findByUserIdOrderByCreatedAtDesc(s, pageable);
    }

    public ResponseEntity<?> updateProfilePicture(String currentUserId, MultipartFile imageFile) {
        String imageUrl = s3Service.uploadFile(imageFile, "profiles");
        return userFeignClient.updateProfileImage(new UserFeignClient.UpdateProfileImageRequest(imageUrl),currentUserId);

    }

    @Transactional
    public Photo createPhoto(String userId, PhotoController.CreatePhotoRequest request) {
        // Upload image to S3
        String imageUrl = s3Service.uploadFile(request.image(), "photos");

        // Process tags
        Set<Tag> tags = processTagNames(new HashSet<>(request.tags()));

        // Create and save photo
        Photo photo = new Photo(
                request.title(),
                request.description(),
                imageUrl,
                userId,
                tags
        );

        return photoRepository.save(photo);
    }
    
    @Transactional
    public Photo updatePhoto(Long id, String currentUserId, PhotoDto photoDto) {
        Photo photo = getPhotoById(id);

        // Check if the current user is the owner of the photo
        if (!photo.getUserId().equals(currentUserId)) {
            throw new AuthenticationException("You are not authorized to update this photo");
        }

        // Update photo details
        if (photoDto.getTitle() != null) {
            photo.setTitle(photoDto.getTitle());
        }

        if (photoDto.getDescription() != null) {
            photo.setDescription(photoDto.getDescription());
        }

        // Update tags if provided
        if (photoDto.getTags() != null) {
            Set<Tag> tags = processTagNames(photoDto.getTags());
            photo.setTags(tags);
        }

        return photoRepository.save(photo);
    }

    @Transactional
    public void deletePhoto(Long id, String currentUserId) {
        Photo photo = getPhotoById(id);

        // Check if the current user is the owner of the photo
        if (!photo.getUserId().equals(currentUserId)) {
            throw new AuthenticationException("You are not authorized to delete this photo");
        }

        // Delete the image from S3
        s3Service.deleteFile(photo.getUrl());

        // Delete the photo from the database
        photoRepository.delete(photo);
    }

    private Set<Tag> processTagNames(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        return tagNames.stream()
                .map(this::getOrCreateTag).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
//
    private Tag getOrCreateTag(String name) {
        // Normalize tag name (lowercase, trim)
        String normalizedName = name.toLowerCase().trim();

        // Find existing tag or create a new one
        return tagRepository.findByName(normalizedName)
                .orElseGet(() -> {
//                    Tag newTag = new Tag(normalizedName);
//                    return tagRepository.save(newTag);
                    return null;
                });
    }
    
//    public Page<Photo> searchPhotos(String query, Pageable pageable) {
//        return photoRepository.searchByTitleOrDescriptionOrTags(query, pageable);
//    }

 public Page<PhotoCardDto> getTrendingPhotoCards(String userId, Pageable pageable) {
    Page<PhotoCardProjection> projections = photoRepository.findTrendingPhotoCards(userId, pageable);
    // Create a set of all unique userIds from the projections to batch fetch user data
    Set<String> userIds = projections.getContent().stream()
            .map(PhotoCardProjection::getUserId)
            .collect(Collectors.toSet());
    log.error(userIds.iterator().next());
    // Batch fetch user information from IAM service
    Map<String, UserExtendedDto> userInfoMap = fetchUserInfoBatch(userIds);
    
    // Map projections to DTOs with user information
    return projections.map(projection -> {
        UserExtendedDto userInfo = userInfoMap.get(projection.getUserId());
        
        return PhotoCardDto.builder()
                .id(projection.getId())
                .url(projection.getUrl())
                .nickname(userInfo.getNickname())
                .userProfileImage(userInfo.getProfileImage())
                .isLiked(projection.getIsLiked())
                .likesCount(projection.getLikesCount())
                .build();
    });
}

/**
 * Fetches user information in batch from IAM service
 * 
 * @param userIds Set of user IDs to fetch information for
 * @return Map of user IDs to UserDto objects
 */
private Map<String, UserExtendedDto> fetchUserInfoBatch(Set<String> userIds) {
    if (userIds.isEmpty()) {
        return new HashMap<>();
    }
    
    try {
        // Call the IAM service to get user information in batch
        ResponseEntity<List<UserExtendedDto>> response = userFeignClient.getUsersByIds(userIds);
        
        if (response.getBody() != null) {
            // Convert list to map for easy lookup
            return response.getBody().stream()
                    .collect(Collectors.toMap(
                            UserExtendedDto::getKeycloakId,
                            userDto -> userDto,
                            (existing, replacement) -> existing
                    ));
        }
    } catch (Exception e) {
        log.error("Error fetching user information from IAM service: {}", e.getMessage());
    }
    
    return new HashMap<>();
}
}