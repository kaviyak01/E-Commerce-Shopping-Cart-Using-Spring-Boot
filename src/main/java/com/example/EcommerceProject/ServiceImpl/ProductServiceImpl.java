package com.example.EcommerceProject.ServiceImpl;

import com.example.EcommerceProject.model.Product;
import com.example.EcommerceProject.model.category;
import com.example.EcommerceProject.repository.ProductRepos;
import com.example.EcommerceProject.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepos productRepos;


    @Override
    public Product save(Product product) {
        return productRepos.save(product);
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepos.findAll();
    }

    @Override
    public Page<Product> getAllProductPagination(int pageNo, int pagesize) {
        Pageable pageable = PageRequest.of(pageNo,pagesize);
        return productRepos.findAll(pageable);
    }



    @Override
    public Boolean deleteProduct(int id) {

        Product product=productRepos.findById(id).orElse(null);
        if(!ObjectUtils.isEmpty(product))
        {
            productRepos.delete(product);
            return true;
        }
        return false;
    }

    @Override
    public Product  getProductById(int id) {
        Product product=productRepos.findById(id).orElse(null);
        return product;
    }


    @Override
    public List<Product> getIsActiveProduct(String category) {
        List<Product> product =null;
        if(ObjectUtils.isEmpty(category))
        {
            product =productRepos.findByIsActiveTrue();
        }
        else{
            product =productRepos.findByCategory(category);
        }
        return product;
    }

    @Override
    public List<Product> getSearchProduct(String ch) {
        return productRepos.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch);
    }

    @Override
    public Page<Product> getSearchProductPagination(String ch,int pageNo, int pagesize) {
        Pageable pageable = PageRequest.of(pageNo,pagesize);
        return productRepos.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,ch,pageable);
    }

    @Override
    public Page<Product> getAllActiveProductPagination(int pageNo, int pagesize,String category) {
        Pageable pageable= PageRequest.of(pageNo,pagesize);

        Page<Product> pageProduct =null;
        if(ObjectUtils.isEmpty(category))
        {
            return pageProduct =productRepos.findByIsActiveTrue(pageable);
        }
        else{
            return pageProduct =productRepos.findByCategory(pageable,category);
        }



    }




}

