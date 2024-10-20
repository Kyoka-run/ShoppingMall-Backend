package com.mall.service.impl;

import com.mall.model.Cart;
import com.mall.model.Customer;
import com.mall.model.Product;
import com.mall.repository.CartRepository;
import com.mall.repository.CustomerRepository;
import com.mall.repository.ProductRepository;
import com.mall.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, CustomerRepository customerRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Cart addProductToCart(Long customerId, Long productId, int quantity) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElse(new Cart(null, customer, new ArrayList<>()));  // Create a new cart with no ID, the customer, and an empty item list

        cart.addProduct(product, quantity);  // Add the product to the cart
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartByCustomerId(Long customerId) {
        return cartRepository.findById(customerId).orElse(null);
    }

    @Override
    @Transactional
    public Cart updateCartItem(Long customerId, Long productId, int quantity) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        cart.updateProduct(product, quantity);  // Update the product quantity
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long customerId) {
        cartRepository.deleteById(customerId);
    }
}

