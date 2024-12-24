package com.mall.service;

import com.mall.exception.BusinessException;
import com.mall.exception.NotFoundException;
import com.mall.model.Product;
import com.mall.repository.ProductRepository;
import com.mall.service.impl.ProductServiceImpl;
import com.mall.service.CacheService;
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
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private ObjectMapper objectMapper;

    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Initialize the service with mocked dependencies
        productService = new ProductServiceImpl(productRepository, cacheService, objectMapper);

        // Create a test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);
    }

    @Test
    void addProduct_Success() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product savedProduct = productService.addProduct(testProduct);

        // Assert
        assertNotNull(savedProduct);
        assertEquals("Test Product", savedProduct.getName());
        assertEquals(99.99, savedProduct.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Product foundProduct = productService.getProductById(1L);

        // Assert
        assertNotNull(foundProduct);
        assertEquals(1L, foundProduct.getId());
        assertEquals("Test Product", foundProduct.getName());
    }

    @Test
    void getProductById_NotFound() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productService.getProductById(99L);
        });
    }

    @Test
    void updateProduct_Success() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(199.99);
        updatedProduct.setStock(20);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        // Act
        Product result = productService.updateProduct(1L, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals(199.99, result.getPrice());
        assertEquals(20, result.getStock());
    }

    @Test
    void updateProduct_InvalidPrice() {
        // Arrange
        Product invalidProduct = new Product();
        invalidProduct.setPrice(-10.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        assertThrows(BusinessException.class, () -> {
            productService.updateProduct(1L, invalidProduct);
        });
    }

    @Test
    void getAllProducts_Success() {
        // Arrange
        List<Product> productList = Arrays.asList(
                testProduct,
                new Product(2L, "Product 2", "Description", 199.99, 20, null)
        );
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<Product> results = productService.getAllProducts();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(productRepository, times(1)).findAll();
    }
}