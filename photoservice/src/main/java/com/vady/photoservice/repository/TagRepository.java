package com.vady.photoservice.repository;

import com.vady.photoservice.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, String> {
    
    Optional<Tag> findByName(String name);
    
    @Query("SELECT t FROM Tag t JOIN t.photos p GROUP BY t ORDER BY COUNT(p) DESC LIMIT :limit")
    List<Tag> findTopTagsByPhotoCount(@Param("limit") int limit);
    
    @Query(value = "SELECT * FROM tags t " +
                  "WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                  "ORDER BY t.name ASC LIMIT :limit", 
           nativeQuery = true)
    List<Tag> findByNameContainingIgnoreCaseOrderByNameAsc(@Param("query") String query, @Param("limit") int limit);

    List<Tag> findAllByOrderByNameAsc();


}