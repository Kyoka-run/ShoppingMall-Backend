package com.mall.controller;

import com.mall.model.Cart;
import com.mall.model.CartItem;
import com.mall.service.CartService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{customerId}/add")
    public Cart addProductToCart(@PathVariable Long customerId, @RequestBody CartItem cartItem) {
        return cartService.addProductToCart(customerId, cartItem.getProductId(), cartItem.getQuantity());
    }

    @GetMapping("/{customerId}")
    public Cart getCartByCustomerId(@PathVariable Long customerId) {
        return cartService.getCartByCustomerId(customerId);
    }

    @PutMapping("/{customerId}/update")
    public Cart updateCartItem(@PathVariable Long customerId, @RequestBody CartItem cartItem) {
        return cartService.updateCartItem(customerId, cartItem.getProductId(), cartItem.getQuantity());
    }

    @DeleteMapping("/{customerId}/clear")
    public void clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
    }
}

