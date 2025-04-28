package com.vady.commentservice;

import com.vady.commentservice.dto.CommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;


    @GetMapping("/{photoId}")
    public ResponseEntity<Page<CommentDto>> getCommentsByPhotoPageable(@PageableDefault(size = 20) Pageable pageable, @PathVariable Long photoId) {
        return ResponseEntity.ok(commentService.getCommentDtosByPhoto(photoId, pageable));
    }

    public record CommentRequest(String content) { }
    //create comment
    @PostMapping("/{photoId}")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest commentRequest, @RequestHeader("X-User-ID") String userId,@PathVariable Long photoId) {
        return ResponseEntity.ok(commentService.createComment(new Comment(commentRequest.content,photoId, userId) ));
    }

    @GetMapping("/count/user/{keycloakId}")
public ResponseEntity<Long> getCommentsCountByUserKeycloakId(@PathVariable String keycloakId) {
    return ResponseEntity.ok(commentService.countCommentsForUserPhotos(keycloakId));
}



}
