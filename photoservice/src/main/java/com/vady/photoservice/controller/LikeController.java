package com.vady.photoservice.controller;

import com.vady.photoservice.dto.LikeDto;
import com.vady.photoservice.dto.UserDto;
import com.vady.photoservice.dto.mapper.LikeMapper;
import com.vady.photoservice.model.Like;
import com.vady.photoservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final LikeMapper likeMapper;


//    @GetMapping("/photos/{photoId}/likes")
//    public ResponseEntity<Stream<UserDto>> getUsersWhoLikedPhoto(
//            @PathVariable Long photoId,
//            @PageableDefault(size = 20) Pageable pageable,
//            @RequestHeader(required = false,name = "X-User-ID") String currentUser) {
//        List<UserDto> users = likeService.getUsersWhoLikedPhoto(photoId, pageable);
//        return ResponseEntity.ok(users.stream().map(userMapper::toDto));
//    }

    @PostMapping("/photos/{photoId}/like")
    public ResponseEntity<LikeDto> likePhoto(
            @PathVariable Long photoId,
            @RequestHeader(name = "X-User-ID") String currentUser) {
        Like like = likeService.likePhoto(currentUser,photoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(likeMapper.toDto(like));
    }

    @DeleteMapping("/photos/{photoId}/like")
    public ResponseEntity<Void> unlikePhoto(
            @PathVariable Long photoId,
            @RequestHeader(name = "X-User-ID") String currentUser) {
        likeService.unlikePhoto(currentUser,photoId);
        return ResponseEntity.noContent().build();
    }
}