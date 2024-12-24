package com.mall.service;

import com.mall.exception.BusinessException;
import com.mall.exception.NotFoundException;
import com.mall.model.Order;
import com.mall.model.OrderItem;
import com.mall.model.Product;
import com.mall.model.User;
import com.mall.repository.OrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    private OrderService orderService;

    private Order testOrder;
    private Product testProduct;
    private User testUser;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, productRepository);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setQuantity(2);
        testOrderItem.setPrice(testProduct.getPrice());

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setShippingAddress("Test Address");
        testOrder.setReceiverName("Test Receiver");
        testOrder.setReceiverPhone("1234567890");
        testOrder.setShippingCost(10.0);

        ArrayList<OrderItem> items = new ArrayList<>();
        items.add(testOrderItem);
        testOrder.setOrderItems(items);
    }

    @Test
    void createOrder_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.createOrder(testOrder);

        assertNotNull(result);
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        assertNotNull(result.getOrderDate());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_InsufficientStock() {
        // 设置订单数量大于库存
        testOrderItem.setQuantity(20);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(BusinessException.class, () -> {
            orderService.createOrder(testOrder);
        });
    }

    @Test
    void payOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.payOrder(1L);

        assertEquals(Order.OrderStatus.PAID, result.getStatus());
        assertNotNull(result.getPaymentTime());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void payOrder_InvalidStatus() {
        testOrder.setStatus(Order.OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(BusinessException.class, () -> {
            orderService.payOrder(1L);
        });
    }

    @Test
    void cancelOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.cancelOrder(1L);

        assertEquals(Order.OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void cancelOrder_InvalidStatus() {
        testOrder.setStatus(Order.OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(1L);
        });
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Order result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getOrderById_NotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            orderService.getOrderById(99L);
        });
    }
}