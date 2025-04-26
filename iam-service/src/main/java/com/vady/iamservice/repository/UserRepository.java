package com.vady.iamservice.repository;

import com.vady.iamservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {// extending JpaRepository interface{
    Optional<User> findByKeycloakId(String keycloakId);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Optional<User> findByNickname(String nickname);

    @Query("SELECT u.keycloakId FROM User u WHERE u.nickname = :nickname")
    Optional<String> findKeycloakIdByNickname(@Param("nickname") String nickname);

    @Query("SELECT u FROM User u WHERE u.keycloakId IN :userIds")
    List<User> findAllByKeycloakId(@Param("userIds") Set<String> userIds);
}
