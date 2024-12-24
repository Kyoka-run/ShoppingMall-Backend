package com.mall.service;

import com.mall.model.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order payOrder(Long orderId);
    List<Order> getAllOrders();
    List<Order> getOrdersByUserId(Long userId);
    Order getOrderById(Long id);
    Order cancelOrder(Long id);
    Order updateOrder(Long id, Order updatedOrder);
}

