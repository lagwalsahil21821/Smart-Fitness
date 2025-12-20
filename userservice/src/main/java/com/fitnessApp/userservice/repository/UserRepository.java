package com.fitnessApp.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitnessApp.userservice.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
    boolean existsByEmail(String email);
}