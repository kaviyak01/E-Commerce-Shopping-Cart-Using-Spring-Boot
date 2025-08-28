package com.example.EcommerceProject.ServiceImpl;

import com.example.EcommerceProject.model.category;
import com.example.EcommerceProject.repository.CategoryRepos;
import com.example.EcommerceProject.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepos categoryRepos;
    @Override
    public category save(category category) {
        return categoryRepos.save(category);
    }

    @Override
    public Boolean existCategory(String name) {
        return categoryRepos.existsByName(name);
    }

    @Override
    public List<category> getAllCategory() {
        return categoryRepos.findAll();
    }

    @Override
    public Boolean deleteCategory(int id) {
         category category=categoryRepos.findById(id).orElse(null);
         if(!ObjectUtils.isEmpty(category))
         {
            categoryRepos.delete(category);
             return true;
         }
        return false;
    }

    @Override
    public category editCategoryById(int id) {
        category category=categoryRepos.findById(id).orElse(null);
        return category;
    }

    @Override
    public List<category> getIsActiveCategory() {
        List<category> categories=categoryRepos.findByIsActiveTrue();
        return categories;
    }

    @Override
    public Page<category> getAllCategory(int pageNo, int pageSize) {
        Pageable pageable= PageRequest.of(pageNo,pageSize);
        return categoryRepos.findAll(pageable);
    }
}
