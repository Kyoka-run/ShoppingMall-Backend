package com.mall.controller;

import com.mall.config.TestRedisConfig;
import com.mall.model.Product;
import com.mall.model.Review;
import com.mall.model.User;
import com.mall.repository.ProductRepository;
import com.mall.repository.ReviewRepository;
import com.mall.repository.UserRepository;
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
public class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;
    private Review testReview;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser = userRepository.save(testUser);

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);
        testProduct = productRepository.save(testProduct);

        testReview = new Review();
        testReview.setCustomer(testUser);
        testReview.setProduct(testProduct);
        testReview.setRating(5);
        testReview.setComment("Great product!");
        testReview = reviewRepository.save(testReview);
    }

    @Test
    @WithMockUser
    void addReview_Success() throws Exception {
        Review newReview = new Review();
        newReview.setProduct(testProduct);
        newReview.setCustomer(testUser);
        newReview.setRating(4);
        newReview.setComment("Good product");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newReview)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Good product"));
    }

    @Test
    @WithMockUser
    void addReview_InvalidRating() throws Exception {
        Review invalidReview = new Review();
        invalidReview.setProduct(testProduct);
        invalidReview.setCustomer(testUser);
        invalidReview.setRating(6); // 无效的评分（应该是1-5）
        invalidReview.setComment("Good product");

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReview)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getReviewsByProduct_Success() throws Exception {
        mockMvc.perform(get("/reviews/product/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].comment").value("Great product!"));
    }

    @Test
    @WithMockUser
    void updateReview_Success() throws Exception {
        testReview.setRating(4);
        testReview.setComment("Updated comment");

        mockMvc.perform(put("/reviews/" + testReview.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testReview)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Updated comment"));
    }

    @Test
    @WithMockUser
    void deleteReview_Success() throws Exception {
        mockMvc.perform(delete("/reviews/" + testReview.getId()))
                .andExpect(status().isOk());

        // 验证评价已被删除
        mockMvc.perform(get("/reviews/product/" + testProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void getAllReviews_Success() throws Exception {
        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].comment").exists());
    }
}