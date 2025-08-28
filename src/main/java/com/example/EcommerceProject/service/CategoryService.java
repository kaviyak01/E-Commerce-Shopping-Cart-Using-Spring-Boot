package com.example.EcommerceProject.service;

import com.example.EcommerceProject.model.category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {

    public category save(category category);

    public Boolean existCategory(String name);

    public List<category> getAllCategory();

    public Boolean deleteCategory(int id);

    public category editCategoryById(int id);

    public List<category> getIsActiveCategory();

    public Page<category> getAllCategory(int pageNo,int pageSize);

}
