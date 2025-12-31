package com.innowise.payment.entity;

public enum OrderStatus {
    PENDS_PAY,
    ON_HOLD,
    PROCESSING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    COMPLETED,
    CANCELED,
    REFUNDED
}
