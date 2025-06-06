package com.vady.photoservice.repository;

import com.vady.photoservice.dto.PhotoCardProjection;
import com.vady.photoservice.dto.UserDto;
import com.vady.photoservice.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Set;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    Page<Photo> findByUserIdOrderByCreatedAtDesc(String s, Pageable pageable);
//    Page<Photo> findAllByOrderByCreatedAtDesc(Pageable pageable);
//
//    Page<Photo> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
//
//    Page<Photo> findByTagsNameOrderByCreatedAtDesc(String tagName, Pageable pageable);
//
//    @Query("SELECT p FROM Photo p WHERE " +
//            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
//            "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
//            "EXISTS (SELECT t FROM p.tags t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')))")
//    Page<Photo> searchByTitleOrDescriptionOrTags(@Param("query") String query, Pageable pageable);
//
//    @Query("SELECT COUNT(p) FROM Photo p WHERE p.userId = :user")
//    long countByUser(@Param("user") Long userId);
//
//    @Query("SELECT p FROM Photo p JOIN p.likes l GROUP BY p ORDER BY COUNT(l) DESC")
//    Page<Photo> findTopLikedPhotos(Pageable pageable);
//
//    @Query("SELECT p FROM Photo p JOIN p.comments c GROUP BY p ORDER BY COUNT(c) DESC")
//    Page<Photo> findTopCommentedPhotos(Pageable pageable);



//@Query(value = "SELECT " +
//        "p.id AS id, " +
//        "p.url AS url, " +
//        "p.user_id AS userId, " +
//        "COUNT(l.id) AS likesCount, " +
//        "CASE " +
//        "    WHEN :userId IS NULL THEN false " +
//        "    WHEN COUNT(CASE WHEN l.user_id = :userId THEN 1 END) > 0 THEN true " +
//        "    ELSE false " +
//        "END AS isLiked " +
//        "FROM photos p " +
//        "JOIN likes l ON p.id = l.photo_id AND l.created_at >= NOW() - INTERVAL '7 days' " +
//        "GROUP BY p.id, p.url " +
//        "ORDER BY likesCount DESC",
//        nativeQuery = true)
//Page<PhotoCardProjection> findTrendingPhotoCards(@Param("userId") String userId, Pageable pageable);
//
//@Query(value = "SELECT " +
//        "p.id AS id, " +
//        "p.url AS url, " +
//        "p.user_id AS userId, " +
//        "COUNT(DISTINCT l.id) AS likesCount, " +
//        "CASE " +
//        "    WHEN :userId IS NULL THEN false " +
//        "    WHEN COUNT(DISTINCT CASE WHEN l.user_id = :userId THEN l.id END) > 0 THEN true " +
//        "    ELSE false " +
//        "END AS isLiked " +
//        "FROM photos p " +
//        "JOIN photo_tags pt ON p.id = pt.photo_id " +
//        "JOIN tags t ON pt.tag_id = t.id " +
//        "LEFT JOIN likes l ON p.id = l.photo_id " +
//        "WHERE t.name = :tagName " +
//        "GROUP BY p.id, p.url " +
//        "ORDER BY likesCount DESC",
//        nativeQuery = true)
//Page<PhotoCardProjection> findPhotoCardsByTagName(@Param("tagName") String tagName, @Param("userId") String userId, Pageable pageable);
//
//@Query(value = "SELECT " +
//        "p.id AS id, " +
//        "p.url AS url, " +
//        "p.user_id AS userId, " +
//        "COUNT(DISTINCT l.id) AS likesCount, " +
//        "CASE " +
//        "    WHEN :userId IS NULL THEN false " +
//        "    WHEN COUNT(DISTINCT CASE WHEN l.user_id = :userId THEN l.id END) > 0 THEN true " +
//        "    ELSE false " +
//        "END AS isLiked " +
//        "FROM photos p " +
//        "JOIN photo_tags pt ON p.id = pt.photo_id " +
//        "JOIN tags t ON pt.tag_id = t.id " +
//        "LEFT JOIN likes l ON p.id = l.photo_id " +
//        "WHERE t.name IN :tagNames " +
//        "GROUP BY p.id, p.url " +
//        "HAVING COUNT(DISTINCT t.name) = :#{#tagNames.size()} " +
//        "ORDER BY likesCount DESC",
//        nativeQuery = true)
//Page<PhotoCardProjection> findPhotoCardsByTagNames(@Param("tagNames") Set<String> tagNames, @Param("userId") String userId, Pageable pageable);

@Query(value = "SELECT " +
        "p.id AS id, " +
        "p.url AS url, " +
        "p.user_id AS userId, " +
        "p.created_at AS createdAt, " +
        "COUNT(l.id) AS likesCount, " +
        "CASE " +
        "    WHEN :userId IS NULL THEN false " +
        "    WHEN COUNT(CASE WHEN l.user_id = :userId THEN 1 END) > 0 THEN true " +
        "    ELSE false " +
        "END AS isLiked " +
        "FROM photos p " +
        "LEFT JOIN likes l ON p.id = l.photo_id AND l.created_at >= NOW() - INTERVAL '7 days' " +
        "GROUP BY p.id, p.url, p.user_id, p.created_at " +
        "ORDER BY likesCount DESC, p.created_at DESC",
        nativeQuery = true)
Page<PhotoCardProjection> findTrendingPhotoCards(@Param("userId") String userId, Pageable pageable);

@Query(value = "SELECT " +
        "p.id AS id, " +
        "p.url AS url, " +
        "p.user_id AS userId, " +
        "p.created_at AS createdAt, " +
        "COUNT(DISTINCT l.id) AS likesCount, " +
        "CASE " +
        "    WHEN :userId IS NULL THEN false " +
        "    WHEN COUNT(DISTINCT CASE WHEN l.user_id = :userId THEN l.id END) > 0 THEN true " +
        "    ELSE false " +
        "END AS isLiked " +
        "FROM photos p " +
        "JOIN photo_tags pt ON p.id = pt.photo_id " +
        "JOIN tags t ON pt.tag_id = t.id " +
        "LEFT JOIN likes l ON p.id = l.photo_id " +
        "WHERE t.name = :tagName " +
        "GROUP BY p.id, p.url, p.user_id, p.created_at " +
        "ORDER BY likesCount DESC, p.created_at DESC",
        nativeQuery = true)
Page<PhotoCardProjection> findPhotoCardsByTagName(@Param("tagName") String tagName, @Param("userId") String userId, Pageable pageable);
long countByUserId(String userId);
@Query(value = "SELECT " +
        "p.id AS id, " +
        "p.url AS url, " +
        "p.user_id AS userId, " +
        "p.created_at AS createdAt, " +
        "COUNT(DISTINCT l.id) AS likesCount, " +
        "CASE " +
        "    WHEN :userId IS NULL THEN false " +
        "    WHEN COUNT(DISTINCT CASE WHEN l.user_id = :userId THEN l.id END) > 0 THEN true " +
        "    ELSE false " +
        "END AS isLiked " +
        "FROM photos p " +
        "JOIN photo_tags pt ON p.id = pt.photo_id " +
        "JOIN tags t ON pt.tag_id = t.id " +
        "LEFT JOIN likes l ON p.id = l.photo_id " +
        "WHERE t.name IN :tagNames " +
        "GROUP BY p.id, p.url, p.user_id, p.created_at " +
        "HAVING COUNT(DISTINCT t.name) = :#{#tagNames.size()} " +
        "ORDER BY likesCount DESC, p.created_at DESC",
        nativeQuery = true)
Page<PhotoCardProjection> findPhotoCardsByTagNames(@Param("tagNames") Set<String> tagNames, @Param("userId") String userId, Pageable pageable);
    
@Query("SELECT p.id FROM Photo p WHERE p.userId = :userId")
List<Long> findPhotoIdsByUserId(@Param("userId") String userId);
//    Page<Photo> findAllByOrderByLikesCountDesc(Pageable pageable);
}
