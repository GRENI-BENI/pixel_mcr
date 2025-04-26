package com.vady.commentservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private String content;
    private Long photoId;
    private Long userId;
    private UserDto user;

    @Data
    public static class UserDto {
        private String nickname;
        private String profilePicture;
    }
}