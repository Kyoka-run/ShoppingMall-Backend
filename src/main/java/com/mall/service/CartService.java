package com.mall.service;

import com.mall.model.Cart;

public interface CartService {
    Cart addProductToCart(Long userId, Long productId, int quantity);
    Cart getCartByUserId(Long userId);
    Cart updateCartItem(Long userId, Long productId, int quantity);
    void clearCart(Long userId);
}
