package com.mall.service.impl;

import com.mall.model.Order;
import com.mall.repository.OrderRepository;
import com.mall.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        // Custom method can be implemented in repository if needed
        return orderRepository.findAll(); // Replace with actual implementation
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public void cancelOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        existingOrder.setTotalPrice(updatedOrder.getTotalPrice());
        return orderRepository.save(existingOrder);
    }
}

