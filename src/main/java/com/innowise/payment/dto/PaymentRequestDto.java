package com.innowise.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innowise.payment.entity.PaymentStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating or updating a payment in REST API requests.
 */
@Data
public class PaymentRequestDto {

    /**
     * Unique identifier of the order from table "orders". Must be not NULL and must contain at least 1 symbol.
     */
    @NotNull
    private Long orderId;

    /**
     * Unique identifier of the user from table "users". Must be not NULL and must contain at least 1 symbol.
     */
    @NotNull
    private Long userId;

    /**
     * The current status of the payment.
     */
    @NotNull
    private PaymentStatus status;

    /**
     * The date when the payment was made.
     */
    @NotNull
    private LocalDateTime timestamp;

    /**
     * The total price of the order. Must be a real number with 2 decimal places.
     */
    @NotNull
    @Digits(integer = 10, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal paymentAmount;
}
