package com.mall.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.exception.NotFoundException;
import com.mall.model.Category;
import com.mall.repository.CategoryRepository;
import com.mall.service.CacheService;
import com.mall.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    // Define cache timeout constants
    private static final long CATEGORY_CACHE_HOURS = 48; // Categories change less frequently

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CacheService cacheService,
                               ObjectMapper objectMapper) {
        this.categoryRepository = categoryRepository;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Category findCategoryById(Long id) {
        String cacheKey = "category:" + id;
        String cachedCategory = cacheService.get(cacheKey);

        if (cachedCategory != null) {
            try {
                return objectMapper.readValue(cachedCategory, Category.class);
            } catch (Exception e) {
                // Continue to database if cache read fails
            }
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));

        try {
            cacheService.set(cacheKey, objectMapper.writeValueAsString(category), CATEGORY_CACHE_HOURS);
        } catch (Exception e) {
            // Log warning but continue operation
        }

        return category;
    }

    @Override
    public List<Category> findAllCategories() {
        String cacheKey = "categories:all";
        String cachedCategories = cacheService.get(cacheKey);

        if (cachedCategories != null) {
            try {
                // Need to specify type reference for List<Category>
                return objectMapper.readValue(cachedCategories,
                        new TypeReference<List<Category>>() {});
            } catch (Exception e) {
                // Continue to database if cache read fails
            }
        }

        List<Category> categories = categoryRepository.findAll();

        try {
            cacheService.set(cacheKey, objectMapper.writeValueAsString(categories), CATEGORY_CACHE_HOURS);
        } catch (Exception e) {
            // Log warning but continue operation
        }

        return categories;
    }

    @Override
    @Transactional
    public Category createCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        // Clear the all categories cache when a new category is created
        cacheService.delete("categories:all");
        return savedCategory;
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        existingCategory.setName(category.getName());
        existingCategory.setProducts(category.getProducts());
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        // Clear both individual and list caches
        cacheService.delete("category:" + id);
        cacheService.delete("categories:all");
    }
}

