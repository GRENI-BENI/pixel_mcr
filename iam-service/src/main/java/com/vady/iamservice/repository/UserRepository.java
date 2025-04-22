package com.vady.iamservice.repository;

import com.vady.iamservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {// extending JpaRepository interface{
    Optional<User> findByKeycloakId(String keycloakId);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
