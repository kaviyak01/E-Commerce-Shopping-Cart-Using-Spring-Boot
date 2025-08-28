package com.example.EcommerceProject.repository;

import com.example.EcommerceProject.model.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepos extends JpaRepository<ProductOrder,Integer> {


   public List<ProductOrder> findByUserId(int userId);

   public ProductOrder findByOrderId(String oid);

   public Page<ProductOrder> findAll(Pageable pageable);

   public Page<ProductOrder> findByOrderId(String oid,Pageable pageable);
}
