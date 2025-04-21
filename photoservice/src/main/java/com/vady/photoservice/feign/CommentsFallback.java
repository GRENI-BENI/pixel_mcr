package com.vady.photoservice.feign;

import com.vady.photoservice.dto.CommentDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentsFallback implements CommentsFeignClient {
    @Override
    public ResponseEntity<List<CommentDto>> getCommentsByPhoto(Long photoId) {
        return null;
    }
}
