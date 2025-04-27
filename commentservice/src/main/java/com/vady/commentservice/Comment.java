package com.vady.commentservice;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "photo_id", nullable = false)
    private Long photoId;

    @Column(name = "user_keycloak_id", nullable = false)
    private String userKeycloakId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Comment(String content, Long photoId, String userKeycloakId) {
        this.content = content;
        this.photoId = photoId;
        this.userKeycloakId = userKeycloakId;
    }
}
