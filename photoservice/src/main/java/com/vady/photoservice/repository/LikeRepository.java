package com.vady.photoservice.repository;

import com.vady.photoservice.model.Like;
import com.vady.photoservice.model.Photo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, String> {
    
    Optional<Like> findByUserIdAndPhoto(String userid, Photo photo);
    
    boolean existsByUserIdAndPhoto(String userid, Photo photo);
    
    long countByPhoto(Photo photo);

    long countByUserId(String userid);

    Page<Like> findAllByPhoto(Photo photo, Pageable pageable);
}