package com.mall.controller;

import com.mall.exception.BusinessException;
import com.mall.exception.NotFoundException;
import com.mall.model.Order;
import com.mall.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            validateShippingInfo(order);
            Order created = orderService.createOrder(order);
            return ResponseEntity.ok(created);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(order);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        try {
            Order cancelled = orderService.cancelOrder(id);
            return ResponseEntity.ok(cancelled);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Order> payOrder(@PathVariable Long id) {
        try {
            Order paid = orderService.payOrder(id);
            return ResponseEntity.ok(paid);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private void validateShippingInfo(Order order) {
        if (order.getShippingAddress() == null || order.getShippingAddress().trim().isEmpty()) {
            throw new BusinessException("Shipping address cannot be empty");
        }
        if (order.getReceiverName() == null || order.getReceiverName().trim().isEmpty()) {
            throw new BusinessException("Receiver name cannot be empty");
        }
        if (order.getReceiverPhone() == null || order.getReceiverPhone().trim().isEmpty()) {
            throw new BusinessException("Receiver phone cannot be empty");
        }
    }
}
