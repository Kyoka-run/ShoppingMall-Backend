package com.mall.service.impl;

import com.mall.model.Cart;
import com.mall.repository.CartRepository;
import com.mall.repository.ProductRepository;
import com.mall.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Cart addProductToCart(Long customerId, Long productId, int quantity) {
        // Logic to add product to cart
        return null; // Replace with actual implementation
    }

    @Override
    public Cart getCartByCustomerId(Long customerId) {
        return cartRepository.findById(customerId).orElse(null);
    }

    @Override
    @Transactional
    public Cart updateCartItem(Long customerId, Long productId, int quantity) {
        // Logic to update cart item
        return null; // Replace with actual implementation
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) {
        cartRepository.deleteById(customerId);
    }
}

