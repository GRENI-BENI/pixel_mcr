package com.vady.photoservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "photo_seq_gen")
    @SequenceGenerator(name = "photo_seq_gen", sequenceName = "photo_seq_gen", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String url;




    private String userId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "photo_tags",
            joinColumns = @JoinColumn(name = "photo_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();



    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public int getLikesCount() {
        return likes.size();
    }



    public Photo(String title, String description, String url, String userId, Set<Tag> tags) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.userId = userId;
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(id, photo.id) && Objects.equals(title, photo.title) && Objects.equals(description, photo.description) && Objects.equals(url, photo.url) && Objects.equals(createdAt, photo.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, url, createdAt);
    }
}