package com.vady.commentservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String content;
    private Long photoId;
    private String userKeycloakId;

    public Comment(String content, Long photoId, String userKeycloakId) {
        this.content = content;
        this.photoId = photoId;
        this.userKeycloakId = userKeycloakId;
    }
}
