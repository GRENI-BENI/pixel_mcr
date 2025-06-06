package com.vady.iamservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String keycloakId;
    private String email;
    private String nickname;
    private String profileImage;
    private LocalDateTime createdAt;
}
