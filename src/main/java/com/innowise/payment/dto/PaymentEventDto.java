package com.innowise.payment.dto;

import com.innowise.payment.entity.PaymentStatus;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
public class PaymentEventDto {
    private Long id;
    private Long orderId;
    private Long userId;
    private PaymentStatus status;
    private LocalDate creationDate;
    private String source;
}
