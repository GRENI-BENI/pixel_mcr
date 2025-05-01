package com.vady.photoservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoCardDto {
    private String id;  // Changed from Long to String to match Photo entity's ID type
    private String url;
    private String nickname;
    private String userProfileImage;
    private boolean likedByCurrentUser;
    private long likesCount;
}
