package com.vady.commentservice;

import com.vady.commentservice.dto.CommentDto;
import com.vady.commentservice.dto.UserExtendedDto;
import com.vady.commentservice.feign.PhotoFeignClient;
import com.vady.commentservice.feign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserFeignClient userFeignClient;
    private final CommentMapper commentMapper;
    private final PhotoFeignClient photoFeignClient;

    public Comment createComment(Comment comment) {

        return commentRepository.save(comment);
    }

public long countCommentsForUserPhotos(String userKeycloakId) {
    // Get all photo IDs for the user
    ResponseEntity<List<Long>> response = photoFeignClient.getPhotoIdsByUserKeycloakId(userKeycloakId);
    List<Long> photoIds = response.getBody();
    
    if (photoIds == null || photoIds.isEmpty()) {
        return 0;
    }
    
    // Count comments for these photos
    return commentRepository.countByPhotoIdIn(photoIds);
}
    public Page<Comment> getCommentsByPhoto(Long photoId, Pageable pageable) {
        return commentRepository.findByPhotoIdOrderByCreatedAtDesc(photoId,pageable);
    }

    // Add this method to the CommentService class
    public List<CommentDto> getCommentDtosByPhotoAsList(Long photoId) {
        List<Comment> comments = commentRepository.findByPhotoIdOrderByCreatedAtDesc(photoId);

        // Extract all unique user IDs
        Set<String> userIds = comments.stream()
                .map(Comment::getUserKeycloakId)
                .collect(Collectors.toSet());

        // Fetch user information in batch
        Map<String, UserExtendedDto> userInfoMap = fetchUserInfoBatch(userIds);

        // Map comments to DTOs with user information
        return comments.stream()
                .map(comment -> commentMapper.entityToDto(comment, userInfoMap))
                .collect(Collectors.toList());
    }

public Page<CommentDto> getCommentDtosByPhoto(Long photoId, Pageable pageable) {
    Page<Comment> commentsPage = getCommentsByPhoto(photoId, pageable);
    
    // Extract all unique user IDs
    Set<String> userIds = commentsPage.getContent().stream()
            .map(Comment::getUserKeycloakId)
            .collect(Collectors.toSet());

    // Fetch user information in batch
    Map<String, UserExtendedDto> userInfoMap = fetchUserInfoBatch(userIds);

    // Map comments to DTOs with user information while preserving pagination
    return commentsPage.map(comment -> commentMapper.entityToDto(comment, userInfoMap));
}

    private Map<String, UserExtendedDto> fetchUserInfoBatch(Set<String> userIds) {
        if (userIds.isEmpty()) {
            return new HashMap<>();
        }

        try {
            ResponseEntity<List<UserExtendedDto>> response = userFeignClient.getUsersByIds(userIds);

            if (response.getBody() != null) {
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
