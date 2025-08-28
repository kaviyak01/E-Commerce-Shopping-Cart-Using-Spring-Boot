package com.example.EcommerceProject.repository;

import com.example.EcommerceProject.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepos extends JpaRepository<AppUser, Integer> {

     public AppUser findByEmail(String username);

     public List<AppUser> findByRole(String role);

     public AppUser findByResetToken(String token);

     public boolean existsByEmail(String email);

}
