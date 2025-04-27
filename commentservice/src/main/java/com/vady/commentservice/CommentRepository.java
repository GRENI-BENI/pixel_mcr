package com.vady.commentservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPhotoIdOrderByCreatedAtDesc(Long photoId, Pageable pageable);
    List<Comment> findByPhotoIdOrderByCreatedAtDesc(Long photoId);
}
