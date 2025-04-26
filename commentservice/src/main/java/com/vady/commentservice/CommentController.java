package com.vady.commentservice;

import com.vady.commentservice.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final AppInfo appConfig;
    private final CommentMapper commentMapper;
    @GetMapping("/photo/{photoId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPhoto(@PathVariable Long photoId) {
        return ResponseEntity.ok(commentService.getCommentDtosByPhoto(photoId));
    }

    public record CommentRequest(Long photoId, String content) { }
    //create comment
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest, @Header("X-User-ID") String userId) {
        return ResponseEntity.ok(commentService.createComment(new Comment(commentRequest.content,commentRequest.photoId, userId) ));
    }



    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
