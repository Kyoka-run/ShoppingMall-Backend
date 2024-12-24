package com.mall.controller;

import com.mall.config.TestRedisConfig;
import com.mall.model.Product;
import com.mall.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void createAndGetProduct() throws Exception {
        Product product = new Product();
        product.setName("Integration Test Product");
        product.setPrice(299.99);
        product.setStock(50);

        // Create product
        String responseJson = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Product"))
                .andExpect(jsonPath("$.price").value(299.99))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Product createdProduct = objectMapper.readValue(responseJson, Product.class);

        // Get product
        mockMvc.perform(get("/products/" + createdProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Product"))
                .andExpect(jsonPath("$.price").value(299.99))
                .andExpect(jsonPath("$.stock").value(50));
    }

    @Test
    @WithMockUser
    void getAllProducts() throws Exception {
        // Create test products
        Product product1 = new Product();
        product1.setName("Test Product 1");
        product1.setPrice(99.99);
        product1.setStock(10);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setPrice(199.99);
        product2.setStock(20);
        productRepository.save(product2);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].name", hasItems("Test Product 1", "Test Product 2")));
    }

    @Test
    @WithMockUser
    void updateProduct() throws Exception {
        // Create initial product
        Product product = new Product();
        product.setName("Initial Product");
        product.setPrice(99.99);
        product.setStock(10);
        Product savedProduct = productRepository.save(product);

        // Update product data
        savedProduct.setName("Updated Product");
        savedProduct.setPrice(149.99);

        mockMvc.perform(put("/products/" + savedProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(149.99));
    }

    @Test
    @WithMockUser
    void deleteProduct() throws Exception {
        // First create a product
        Product product = new Product();
        product.setName("Product to Delete");
        product.setPrice(99.99);
        product.setStock(10);
        Product savedProduct = productRepository.save(product);

        // Then delete it
        mockMvc.perform(delete("/products/" + savedProduct.getId()))
                .andExpect(status().isOk());

        // Verify it was deleted
        mockMvc.perform(get("/products/" + savedProduct.getId()))
                .andExpect(status().isNotFound());
    }
}