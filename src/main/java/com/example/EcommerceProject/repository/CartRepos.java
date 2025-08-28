package com.example.EcommerceProject.repository;

import com.example.EcommerceProject.model.cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepos extends JpaRepository<cart,Integer> {

     public cart findByProductIdAndUserId(int productId,int UserId);

     public int countByUserId(int userId);

     public List<cart> findByUserId(int userId);
}
