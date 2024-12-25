package com.mall.service;

import com.mall.exception.NotFoundException;
import com.mall.model.Category;
import com.mall.repository.CategoryRepository;
import com.mall.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private ObjectMapper objectMapper;

    private CategoryService categoryService;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, cacheService, objectMapper);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(testCategory);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository).save(any(Category.class));
        verify(cacheService).delete("categories:all");
    }

    @Test
    void findAllCategories_Success() {
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> results = categoryService.findAllCategories();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Electronics", results.get(0).getName());
    }

    @Test
    void findCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Category result = categoryService.findCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Electronics", result.getName());
    }

    @Test
    void findCategoryById_NotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            categoryService.findCategoryById(99L);
        });
    }

    @Test
    void updateCategory_Success() {
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertNotNull(result);
        assertEquals("Updated Electronics", result.getName());
    }

    @Test
    void deleteCategory_Success() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
        verify(cacheService).delete("category:1");
        verify(cacheService).delete("categories:all");
    }
}