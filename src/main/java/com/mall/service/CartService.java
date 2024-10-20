package com.mall.service;

import com.mall.model.Cart;

public interface CartService {
    Cart addProductToCart(Long customerId, Long productId, int quantity);
    Cart getCartByCustomerId(Long customerId);
    Cart updateCartItem(Long customerId, Long productId, int quantity);
    void clearCart(Long customerId);
}
