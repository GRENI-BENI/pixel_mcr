package com.vady.photoservice.feign;

import com.vady.photoservice.dto.CommentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
public class CommentsFallback implements CommentsFeignClient {
    @Override
    public ResponseEntity<List<CommentDto>> getCommentsByPhoto(Long photoId) {
        return null;
    }

    @Override
    public ResponseEntity<Long> getCommentsCountByUserKeycloakId(String keycloakId) {
        log.error("Failed to get comments count for user with keycloak ID: {}", keycloakId);
        return ResponseEntity.ok(0L);
    }

}
