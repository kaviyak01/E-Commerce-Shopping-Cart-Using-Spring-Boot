package com.example.EcommerceProject.repository;

import com.example.EcommerceProject.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepos extends JpaRepository<Product,Integer> {

   public List<Product> findByIsActiveTrue();

   public Page<Product> findByIsActiveTrue(Pageable pageable);

   public  List<Product> findByCategory(String category);

   public List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch,String ch2);

   public  Page<Product> findByCategory(Pageable pageable,String category);

   public Page<Product> findAll(Pageable pageable);

   public Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch,String ch2,Pageable pageable);
}
