package com.mall.controller;

import com.mall.config.TestRedisConfig;
import com.mall.model.*;
import com.mall.repository.OrderRepository;
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
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // 清空所有测试数据
        orderRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();

        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setEmail("test@example.com");
        testUser = userRepository.save(testUser);

        // 创建测试商品
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);
        testProduct = productRepository.save(testProduct);

        // 创建测试订单
        testOrder = new Order();
        testOrder.setUser(testUser);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setOrderDate(new Date());
        testOrder.setTotalPrice(99.99);
        testOrder.setShippingAddress("Test Address");
        testOrder.setReceiverName("Test Receiver");
        testOrder.setReceiverPhone("1234567890");
        testOrder.setShippingCost(10.0);
        testOrder.setOrderItems(new ArrayList<>());

        // 添加订单项
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(1);
        orderItem.setPrice(testProduct.getPrice());
        orderItem.setOrder(testOrder);
        testOrder.getOrderItems().add(orderItem);

        testOrder = orderRepository.save(testOrder);
    }

    @Test
    @WithMockUser
    void createOrder_Success() throws Exception {
        Order newOrder = new Order();
        newOrder.setUser(testUser);
        newOrder.setShippingAddress("New Address");
        newOrder.setReceiverName("New Receiver");
        newOrder.setReceiverPhone("0987654321");
        newOrder.setShippingCost(10.0);

        OrderItem newItem = new OrderItem();
        newItem.setProduct(testProduct);
        newItem.setQuantity(1);
        newItem.setPrice(testProduct.getPrice());

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(newItem);
        newOrder.setOrderItems(items);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.shippingAddress").value("New Address"));
    }

    @Test
    @WithMockUser
    void getOrderById_Success() throws Exception {
        mockMvc.perform(get("/orders/" + testOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser
    void payOrder_Success() throws Exception {
        mockMvc.perform(post("/orders/" + testOrder.getId() + "/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paymentTime").isNotEmpty());
    }

    @Test
    @WithMockUser
    void cancelOrder_Success() throws Exception {
        mockMvc.perform(post("/orders/" + testOrder.getId() + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser
    void createOrder_InvalidShippingInfo() throws Exception {
        Order invalidOrder = new Order();
        invalidOrder.setUser(testUser);
        // 缺少收货地址和收货人信息

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getOrderById_NotFound() throws Exception {
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());
    }
}