package com.mall.service.impl;

import com.mall.exception.BusinessException;
import com.mall.exception.NotFoundException;
import com.mall.model.Order;
import com.mall.model.OrderItem;
import com.mall.model.Product;
import com.mall.repository.OrderRepository;
import com.mall.repository.ProductRepository;
import com.mall.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(Order.OrderStatus.PENDING);
        order.setOrderDate(new Date());

        // Calculate total price
        double totalPrice = order.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalPrice(totalPrice + order.getShippingCost());

        // Check stock availability
        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + item.getProduct().getId()));

            if (product.getStock() < item.getQuantity()) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order payOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        // Only pending orders can be paid
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException("Order cannot be paid in current status: " + order.getStatus());
        }

        // Update order status and payment time
        order.setStatus(Order.OrderStatus.PAID);
        order.setPaymentTime(new Date());

        // Update product stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        return orderRepository.save(order);
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
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        // Can only cancel pending orders
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException("Order cannot be cancelled");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        return orderRepository.save(order);
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

