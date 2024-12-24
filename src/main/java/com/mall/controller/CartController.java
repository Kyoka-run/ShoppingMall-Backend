package com.mall.controller;

import com.mall.exception.BusinessException;
import com.mall.exception.NotFoundException;
import com.mall.model.Cart;
import com.mall.model.CartItem;
import com.mall.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Cart> getCartByCustomerId(@PathVariable Long customerId) {
        try {
            Cart cart = cartService.getCartByUserId(customerId);
            return ResponseEntity.ok(cart);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{customerId}/add")
    public ResponseEntity<Cart> addProductToCart(@PathVariable Long customerId,
                                                 @RequestBody CartItem cartItem) {
        try {
            Cart updated = cartService.addProductToCart(customerId,
                    cartItem.getProductId(), cartItem.getQuantity());
            return ResponseEntity.ok(updated);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{customerId}/update")
    public ResponseEntity<Cart> updateCartItem(@PathVariable Long customerId,
                                               @RequestBody CartItem cartItem) {
        try {
            Cart updated = cartService.updateCartItem(customerId,
                    cartItem.getProductId(), cartItem.getQuantity());
            return ResponseEntity.ok(updated);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{customerId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok().build();
    }
}

