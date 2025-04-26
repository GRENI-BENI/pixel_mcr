package com.vady.iamservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private Long id;
    
    @Column(name = "keycloak_id", unique = true, nullable = false)
    private String keycloakId;
    
    @Column(name = "nickname")
    private String nickname;
    
    private String email;
    private boolean emailVerified;

    @Column(length = 1000)
    private String about;

    @Column(name = "profile_image", length = 1000000, nullable = false)
    private String profileImage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "following_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<User> following = new HashSet<>();

    public User(String keycloakId, String nickname, String email, boolean emailVerified, String about, String profileImage) {
        this.keycloakId = keycloakId;
        this.nickname = nickname;
        this.email = email;
        this.emailVerified = emailVerified;
        this.about = about;
        this.profileImage = profileImage;
    }
}