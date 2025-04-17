package com.vady.photoservice.repository;

import com.vady.photoservice.model.Like;
import com.vady.photoservice.model.Photo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, String> {
    
    Optional<Like> findByUserIdAndPhoto(Long userid, Photo photo);
    
    boolean existsByUserIdAndPhoto(Long userid, Photo photo);
    
    long countByPhoto(Photo photo);

    long countByUserId(Long userid);

    Page<Like> findAllByPhoto(Photo photo, Pageable pageable);
}