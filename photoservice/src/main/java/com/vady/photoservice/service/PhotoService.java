package com.vady.photoservice.service;

import com.vady.photoservice.dto.PhotoCardDto;
import com.vady.photoservice.dto.PhotoCardProjection;
import com.vady.photoservice.dto.PhotoDto;
import com.vady.photoservice.exception.AuthenticationException;
import com.vady.photoservice.exception.ResourceNotFoundException;
import com.vady.photoservice.model.Photo;
import com.vady.photoservice.model.Tag;
import com.vady.photoservice.repository.PhotoRepository;
import com.vady.photoservice.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {
    
    private final PhotoRepository photoRepository;
    private final TagRepository tagRepository;
    private final S3Service s3Service;
    
//    public Page<Photo> getAllPhotos(Pageable pageable) {
//        return photoRepository.findAllByOrderByCreatedAtDesc(pageable);
//    }
//
//    public Page<Photo> getPhotosByUser(Long userId, Pageable pageable) {
//        return photoRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
//    }
    
// public Page<PhotoCardDto> getPhotosByTag(Set<String> tagNames, Long currentUserId, Pageable pageable) {
//     Page<PhotoCardProjection> projections;
//
//    if (tagNames.size() == 1) {
//        // If only one tag, use the existing repository method
//        String tagName = tagNames.iterator().next();
//        projections = photoRepository.findPhotoCardsByTagName(tagName, currentUserId, pageable);
//    } else {
//        // For multiple tags
//        projections = photoRepository.findPhotoCardsByTagNames(tagNames, currentUserId, pageable);
//    }
//
//    return projections.map(projection -> PhotoCardDto.builder()
//            .id(projection.getId())
//            .url(projection.getUrl())
//            .nickname(projection.getNickname())
//            .userProfileImage(projection.getUserProfileImage())
//            .isLiked(projection.getIsLiked())
//            .likesCount(projection.getLikesCount())
//            .build());
//}
    
    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo ","id",String.valueOf(id)));
    }

    public Photo getById(Long id) {
        return photoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Photo ","id",String.valueOf(id)));
    }

//    @Transactional
//    public Photo createPhoto(Long userId, PhotoController.CreatePhotoRequest request) {
//        // Upload image to S3
//        String imageUrl = s3Service.uploadFile(request.image(), "photos");
//
//        // Process tags
//        Set<Tag> tags = processTagNames(new HashSet<>(request.tags()));
//
//        // Create and save photo
//        Photo photo = new Photo(
//                request.title(),
//                request.description(),
//                imageUrl,
//                userId,
//                tags
//        );
//
//        return photoRepository.save(photo);
//    }
    
//    @Transactional
//    public Photo updatePhoto(Long id, Long currentUserId, PhotoDto photoDto) {
//        Photo photo = getPhotoById(id);
//
//        // Check if the current user is the owner of the photo
//        if (!photo.getUserId().equals(currentUserId)) {
//            throw new AuthenticationException("You are not authorized to update this photo");
//        }
//
//        // Update photo details
//        if (photoDto.getTitle() != null) {
//            photo.setTitle(photoDto.getTitle());
//        }
//
//        if (photoDto.getDescription() != null) {
//            photo.setDescription(photoDto.getDescription());
//        }
//
//        // Update tags if provided
//        if (photoDto.getTags() != null) {
//            Set<Tag> tags = processTagNames(photoDto.getTags());
//            photo.setTags(tags);
//        }
//
//        return photoRepository.save(photo);
//    }
//
//    @Transactional
//    public void deletePhoto(Long id, Long currentUserId) {
//        Photo photo = getPhotoById(id);
//
//        // Check if the current user is the owner of the photo
//        if (!photo.getUserId().equals(currentUserId)) {
//            throw new AuthenticationException("You are not authorized to delete this photo");
//        }
//
//        // Delete the image from S3
//        s3Service.deleteFile(photo.getUrl());
//
//        // Delete the photo from the database
//        photoRepository.delete(photo);
//    }
//
//    private Set<Tag> processTagNames(Set<String> tagNames) {
//        if (tagNames == null || tagNames.isEmpty()) {
//            return new HashSet<>();
//        }
//
//        return tagNames.stream()
//                .map(this::getOrCreateTag)
//                .collect(Collectors.toSet());
//    }
//
//    private Tag getOrCreateTag(String name) {
//        // Normalize tag name (lowercase, trim)
//        String normalizedName = name.toLowerCase().trim();
//
//        // Find existing tag or create a new one
//        return tagRepository.findByName(normalizedName)
//                .orElseGet(() -> {
//                    Tag newTag = new Tag(normalizedName);
//                    return tagRepository.save(newTag);
//                });
//    }
    
//    public Page<Photo> searchPhotos(String query, Pageable pageable) {
//        return photoRepository.searchByTitleOrDescriptionOrTags(query, pageable);
//    }

//    public Page<PhotoCardDto> getTrendingPhotoCards(Long userId, Pageable pageable) {
//        Page<PhotoCardProjection> projections = photoRepository.findTrendingPhotoCards(userId, pageable);
//
//        return projections.map(projection -> PhotoCardDto.builder()
//                .id(projection.getId())
//                .url(projection.getUrl())
//                .nickname(projection.getNickname())
//                .userProfileImage(projection.getUserProfileImage())
//                .isLiked(projection.getIsLiked())
//                .likesCount(projection.getLikesCount())
//                .build());
//    }
}