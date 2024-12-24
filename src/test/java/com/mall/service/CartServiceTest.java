package com.mall.service;

import com.mall.exception.NotFoundException;
import com.mall.model.Cart;
import com.mall.model.Product;
import com.mall.model.User;
import com.mall.repository.CartRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import com.mall.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private ObjectMapper objectMapper;

    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartRepository, productRepository, userRepository, cacheService, objectMapper);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setCustomer(testUser);
        testCart.setItems(new ArrayList<>());
    }

    @Test
    void getCartByUserId_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        Cart result = cartService.getCartByUserId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(cartRepository).findByUserId(1L);
    }

    @Test
    void getCartByUserId_NotFound_CreatesNewCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.getCartByUserId(1L);

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addProductToCart_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        Cart result = cartService.addProductToCart(1L, 1L, 2);

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addProductToCart_ProductNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            cartService.addProductToCart(1L, 99L, 2);
        });
    }

    @Test
    void clearCart_Success() {
        doNothing().when(cartRepository).deleteByUserId(1L);
        cartService.clearCart(1L);
        verify(cartRepository).deleteByUserId(1L);
    }
}