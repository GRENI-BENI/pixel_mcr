package com.vady.iamservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "platforms")
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "platforms_seq")
    @SequenceGenerator(name = "platforms_seq", sequenceName = "platforms_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000000, nullable = false)
    private String icon;

    @Column(nullable = false)
    private String baseUrl;

    public Platform(String name, String icon, String baseUrl) {
        this.name = name;
        this.icon = icon;
        this.baseUrl = baseUrl;
    }
}