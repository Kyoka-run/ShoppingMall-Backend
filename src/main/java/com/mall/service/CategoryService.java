package com.mall.service;

import com.mall.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    List<Category> findAllCategories();
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    Category findCategoryById(Long id);
}

