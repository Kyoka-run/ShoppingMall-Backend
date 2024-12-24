package com.mall.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.exception.BusinessException;
import com.mall.exception.NotFoundException;
import com.mall.model.Product;
import com.mall.repository.ProductRepository;
import com.mall.service.CacheService;
import com.mall.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              CacheService cacheService,
                              ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        // Try to get from cache first
        String cacheKey = "product:" + id;
        String cachedProduct = cacheService.get(cacheKey);

        if (cachedProduct != null) {
            try {
                // Parse cached JSON string back to Product object
                return objectMapper.readValue(cachedProduct, Product.class);
            } catch (Exception e) {
                // If we have any JSON parsing issues, we'll treat it as a cache miss and continue to fetch from database
                // We can log this as a warning but won't throw an exception
            }
        }

        // If not in cache, get from database
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        try {
            // Convert Product object to JSON string and cache it
            cacheService.set(cacheKey, objectMapper.writeValueAsString(product), 24);
        } catch (Exception e) {
            // If we can't cache, we'll just continue without caching, This is not a critical error that should stop the operation
            // We can log this as a warning using your logging system
        }

        return product;
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
        // Delete from cache when product is deleted
        cacheService.delete("product:" + id);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);

        if (updatedProduct.getPrice() < 0) {
            throw new BusinessException("Product price cannot be negative");
        }
        if (updatedProduct.getStock() < 0) {
            throw new BusinessException("Product stock cannot be negative");
        }

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setDescription(updatedProduct.getDescription());

        return productRepository.save(existingProduct);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }

        // Search by name or description
        return productRepository.findByNameContainingOrDescriptionContaining(
                keyword, keyword
        );
    }

    @Override
    public List<Product> getProductsByCategory(String categoryName) {
        // Get products by category
        return productRepository.findByCategoryName(categoryName);
    }
}
