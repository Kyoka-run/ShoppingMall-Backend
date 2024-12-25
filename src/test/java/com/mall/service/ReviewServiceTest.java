package com.mall.service;

import com.mall.exception.BusinessException;
import com.mall.model.Product;
import com.mall.model.Review;
import com.mall.model.User;
import com.mall.repository.ReviewRepository;
import com.mall.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    private ReviewService reviewService;

    private Review testReview;
    private Product testProduct;
    private User testUser;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewServiceImpl(reviewRepository);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setCustomer(testUser);
        testReview.setProduct(testProduct);
        testReview.setRating(4);
        testReview.setComment("Good product");
    }

    @Test
    void addReview_Success() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewService.addReview(testReview);

        assertNotNull(result);
        assertEquals(4, result.getRating());
        assertEquals("Good product", result.getComment());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void addReview_InvalidRating() {
        testReview.setRating(6);

        assertThrows(BusinessException.class, () -> {
            reviewService.addReview(testReview);
        });
    }

    @Test
    void addReview_EmptyComment() {
        testReview.setComment("");

        assertThrows(BusinessException.class, () -> {
            reviewService.addReview(testReview);
        });
    }

    @Test
    void findAllReviews_Success() {
        List<Review> reviewList = Arrays.asList(testReview);
        when(reviewRepository.findAll()).thenReturn(reviewList);

        List<Review> results = reviewService.findAllReviews();

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(reviewRepository).findAll();
    }

    @Test
    void findReviewsByProductId_Success() {
        List<Review> reviewList = Arrays.asList(testReview);
        when(reviewRepository.findByProductId(1L)).thenReturn(reviewList);

        List<Review> results = reviewService.findReviewsByProductId(1L);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(reviewRepository).findByProductId(1L);
    }

    @Test
    void updateReview_Success() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        testReview.setRating(5);
        testReview.setComment("Updated comment");

        Review result = reviewService.updateReview(1L, testReview);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Updated comment", result.getComment());
    }

    @Test
    void deleteReview_Success() {
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.deleteReview(1L);

        verify(reviewRepository).deleteById(1L);
    }
}