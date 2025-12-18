package com.innowise.payment.dto;

import com.innowise.payment.entity.OrderStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderEventDto {
    private Long orderId;
    private Long userId;
    private OrderStatus status;
    private LocalDate creationDate;
    private String source;
}
