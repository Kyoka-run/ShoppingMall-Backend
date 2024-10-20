package com.mall.service;

import com.mall.model.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> getAllOrders();
    List<Order> getOrdersByCustomerId(Long customerId);
    Order getOrderById(Long id);
    void cancelOrder(Long id);
    Order updateOrder(Long id, Order updatedOrder);
}

