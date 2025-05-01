package com.vady.iamservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user_donations")
public class UserDonation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_donations_seq")
    @SequenceGenerator(name = "user_donations_seq", sequenceName = "user_donations_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(nullable = false)
    private String donationLink;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UserDonation(User user, Platform platform, String donationLink) {
        this.user = user;
        this.platform = platform;
        this.donationLink = donationLink;
    }
}