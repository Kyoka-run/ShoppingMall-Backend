package com.mall.service;

import com.mall.model.OrderItem;

import java.util.List;

public interface OrderItemService {
    List<OrderItem> findAllOrderItems();
    OrderItem findOrderItemById(Long id);
    OrderItem createOrderItem(OrderItem orderItem);
    OrderItem updateOrderItem(Long id, OrderItem orderItem);
    void deleteOrderItem(Long id);
}
