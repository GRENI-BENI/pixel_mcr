package com.vady.photoservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDto {
    
    private Long id;
    
    @NotBlank(message = "Photo title cannot be empty")
    @Size(max = 255, message = "Photo title cannot exceed 255 characters")
    private String title;
    
    @Size(max = 1000, message = "Photo description cannot exceed 1000 characters")
    private String description;
    
    private String url;
    
    private Long userId;
    
    private String nickname;
    
    private String userProfileImage;
    
    private Set<String> tags;
    
    private int likesCount;
    
    private int commentsCount;
    
    private boolean likedByCurrentUser;
    
    private LocalDateTime createdAt;
}