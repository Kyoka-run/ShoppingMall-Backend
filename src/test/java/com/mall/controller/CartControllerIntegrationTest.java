package com.mall.controller;

import com.mall.config.TestRedisConfig;
import com.mall.model.Cart;
import com.mall.model.CartItem;
import com.mall.model.Product;
import com.mall.model.User;
import com.mall.repository.CartRepository;
import com.mall.repository.ProductRepository;
import com.mall.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        // Clear repositories
        cartRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser = userRepository.save(testUser);

        // Create test product
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);
        testProduct = productRepository.save(testProduct);

        // Create test cart
        testCart = new Cart();
        testCart.setCustomer(testUser);
        testCart.setItems(new ArrayList<>());
        testCart = cartRepository.save(testCart);
    }

    @Test
    @WithMockUser
    void getCartByCustomerId() throws Exception {
        mockMvc.perform(get("/carts/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCart.getId()));
    }

    @Test
    @WithMockUser
    void addProductToCart() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);

        mockMvc.perform(post("/carts/" + testUser.getId() + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].productId").value(testProduct.getId()));
    }

    @Test
    @WithMockUser
    void updateCartItem() throws Exception {
        // First add an item
        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(2);

        // Then update it
        mockMvc.perform(put("/carts/" + testUser.getId() + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItem)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void clearCart() throws Exception {
        mockMvc.perform(delete("/carts/" + testUser.getId() + "/clear"))
                .andExpect(status().isOk());
    }
}
