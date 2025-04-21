package com.vady.photoservice.feign;

import com.vady.photoservice.dto.CommentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "commentservice", fallback = CommentsFallback.class)
public interface CommentsFeignClient {

    @GetMapping("/comments/{photoId}")
    ResponseEntity<List<CommentDto>> getCommentsByPhoto(@PathVariable Long photoId);
}
