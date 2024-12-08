package com.mall.exception;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "Success"),
    PARAM_ERROR(400, "Parameter error"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_ERROR(500, "Internal server error"),

    // Business codes start from 1000
    USER_NOT_FOUND(1001, "User not found"),
    PRODUCT_NOT_FOUND(1002, "Product not found"),
    ORDER_NOT_FOUND(1003, "Order not found"),
    CART_NOT_FOUND(1004, "Cart not found"),
    STOCK_NOT_ENOUGH(1005, "Product stock not enough"),
    INVALID_ORDER_STATUS(1006, "Invalid order status"),
    DUPLICATE_USERNAME(1007, "Username already exists"),
    INVALID_PASSWORD(1008, "Invalid password"),
    PAYMENT_FAILED(1009, "Payment failed");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
