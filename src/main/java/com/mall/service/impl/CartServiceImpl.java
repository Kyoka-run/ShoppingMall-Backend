package com.mall.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.exception.BusinessException;
import com.mall.exception.NotFoundException;
import com.mall.model.Cart;
import com.mall.model.User;
import com.mall.model.Product;
import com.mall.repository.CartRepository;
import com.mall.repository.UserRepository;
import com.mall.repository.ProductRepository;
import com.mall.service.CacheService;
import com.mall.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    private static final long CART_CACHE_HOURS = 24;

    public CartServiceImpl(CartRepository cartRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           CacheService cacheService,
                           ObjectMapper objectMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Cart addProductToCart(Long userId, Long productId, int quantity) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

        cart.addProduct(product, quantity);
        Cart updatedCart = cartRepository.save(cart);

        // Update cache with new cart state
        try {
            cacheService.set("cart:user:" + userId,
                    objectMapper.writeValueAsString(updatedCart), CART_CACHE_HOURS);
        } catch (Exception e) {
            // Log warning but continue operation
        }

        return updatedCart;
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        String cacheKey = "cart:user:" + userId;
        String cachedCart = cacheService.get(cacheKey);

        if (cachedCart != null) {
            try {
                return objectMapper.readValue(cachedCart, Cart.class);
            } catch (Exception e) {
                // Continue to database if cache read fails
            }
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createNewCart(userId));

        try {
            cacheService.set(cacheKey, objectMapper.writeValueAsString(cart), CART_CACHE_HOURS);
        } catch (Exception e) {
            // Log warning but continue operation
        }

        return cart;
    }

    @Override
    @Transactional
    public Cart updateCartItem(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Simple quantity check
        if (quantity <= 0) {
            throw new BusinessException("Quantity must be greater than 0");
        }

        cart.updateProduct(product, quantity);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
        // Remove cart from cache
        cacheService.delete("cart:user:" + userId);
    }

    private Cart createNewCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Cart newCart = new Cart(null, user, new ArrayList<>());
        return cartRepository.save(newCart);
    }
}


