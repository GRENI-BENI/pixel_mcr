package com.vady.iamservice.repository;

import com.vady.iamservice.model.User;
import com.vady.iamservice.model.UserDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDonationRepository extends JpaRepository<UserDonation, Long> {
    List<UserDonation> findByUser(User user);
    void deleteByUserAndId(User user, Long id);
}