package com.vady.iamservice.repository;

import com.vady.iamservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>  {// extending JpaRepository interface{
}
