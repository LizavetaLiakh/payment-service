package com.innowise.payment.dto;

import com.innowise.payment.entity.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for returning payment data in REST API responses.
 */
@Data
public class PaymentResponseDto {

    /**
     * Unique identifier of the payment.
     */
    private Long id;

    /**
     * Unique identifier of the {@code Order} from table "orders".
     */
    private Long orderId;

    /**
     * Unique identifier of the {@code User} from table "users".
     */
    private Long userId;

    /**
     * The current status of the payment.
     */
    private PaymentStatus status;

    /**
     * The date when the payment was made.
     */
    private LocalDateTime timestamp;

    /**
     * The total price of the order.
     */
    private BigDecimal paymentAmount;
}
