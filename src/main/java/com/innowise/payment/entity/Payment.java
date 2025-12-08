package com.innowise.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity that stores information about payments.
 */
@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    /**
     * Unique identifier of the payment. Generates automatically by the database.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier of the order from table "orders" in orders' database.
     */
    @Column(name = "order_id", nullable = false)
    private Long orderId;

    /**
     * Unique identifier of the user from table "users" in users' database.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * The status of the payment.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    /**
     * Timestamp when the payment was made.
     */
    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * The total price of the order.
     */
    @Column(name = "payment_amount", nullable = false)
    private BigDecimal paymentAmount;
}
