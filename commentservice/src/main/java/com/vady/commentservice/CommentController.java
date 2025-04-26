package com.vady.commentservice;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final AppInfo appConfig;
    private final CommentMapper commentMapper;
    @GetMapping("/{photoId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPhoto(@PathVariable Long photoId) {
       return ResponseEntity.ok(commentService.getCommentsByPhoto(photoId).stream().map(commentMapper::entityToDto).toList());
    }

    public record CommentRequest(Long photoId, String content) { }
    //create comment
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(commentService.createComment(new Comment(commentRequest.content,commentRequest.photoId, commentRequest.userId) ));
    }

    String testStr;  // read from application.properties file

    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
