package com.vady.commentservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private String content;
    private Long userId;
    private String nickname;
    private String userProfileImage;
    private LocalDateTime createdAt;

    @Data
    public static class UserDto {

    }
}