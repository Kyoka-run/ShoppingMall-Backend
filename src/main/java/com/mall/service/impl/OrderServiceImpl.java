package com.mall.service.impl;

import com.mall.model.Order;
import com.mall.model.OrderItem;
import com.mall.repository.OrderRepository;
import com.mall.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        // order created time
        order.setOrderDate(new Date());

        // set status
        order.setStatus("CREATED");

        // calculate money
        double totalPrice = calculateItemsTotal(order.getOrderItems());
        order.setTotalPrice(totalPrice + order.getShippingCost());

        return orderRepository.save(order);
    }

    private double calculateItemsTotal(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);  // Use a custom repository method
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus().equals("CREATED")) {
            order.setStatus("CANCELLED");
            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // update info
        existingOrder.setTotalPrice(updatedOrder.getTotalPrice());
        existingOrder.setStatus(updatedOrder.getStatus());

        // update shipping info
        existingOrder.setShippingAddress(updatedOrder.getShippingAddress());
        existingOrder.setShippingMethod(updatedOrder.getShippingMethod());
        existingOrder.setShippingCost(updatedOrder.getShippingCost());
        existingOrder.setReceiverName(updatedOrder.getReceiverName());
        existingOrder.setReceiverPhone(updatedOrder.getReceiverPhone());

        return orderRepository.save(existingOrder);
    }
}

