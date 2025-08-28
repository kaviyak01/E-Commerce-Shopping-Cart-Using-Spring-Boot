package com.example.EcommerceProject.service;

import com.example.EcommerceProject.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    public Product save(Product product);

    public List<Product> getAllProduct();

    public Boolean deleteProduct(int id);

    public Product getProductById(int id);

    public List<Product> getIsActiveProduct(String category);

    public List<Product> getSearchProduct(String ch);

    public Page<Product> getAllActiveProductPagination(int pageNo,int pagesize,String category);

    public Page<Product> getAllProductPagination(int pageNo,int pagesize);

    public Page<Product> getSearchProductPagination(String ch,int pageNo,int pagesize );


}
