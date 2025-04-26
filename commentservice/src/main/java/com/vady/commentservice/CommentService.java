package com.vady.commentservice;

import com.vady.commentservice.dto.CommentDto;
import com.vady.commentservice.dto.UserExtendedDto;
import com.vady.commentservice.feign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }


    public List<Comment> getCommentsByPhoto(Long photoId) {
        return commentRepository.findAllByPhotoId(photoId);
    }

    public List<CommentDto> getCommentDtosByPhoto(Long photoId) {
        List<Comment> comments = getCommentsByPhoto(photoId);

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
