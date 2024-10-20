package com.mall.service;

import com.mall.model.Review;

import java.util.List;

public interface ReviewService {
    Review addReview(Review review);
    List<Review> findAllReviews();
    List<Review> findReviewsByProductId(Long productId);
    Review updateReview(Long id, Review review);
    void deleteReview(Long id);
}

