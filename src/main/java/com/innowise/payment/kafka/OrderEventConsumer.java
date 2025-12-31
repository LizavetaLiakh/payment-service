package com.innowise.payment.kafka;

import com.innowise.payment.dto.OrderEventDto;
import com.innowise.payment.dto.PaymentRequestDto;
import com.innowise.payment.entity.PaymentStatus;
import com.innowise.payment.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderEventConsumer {

    private final PaymentProducer paymentProducer;
    private final PaymentService paymentService;

    public OrderEventConsumer(PaymentProducer paymentProducer, PaymentService paymentService) {
        this.paymentProducer = paymentProducer;
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "create_order_v2", groupId = "payment-service-group-v2")
    public void handleCreateOrder(OrderEventDto orderEvent) {
        if (!"order-service".equals(orderEvent.getSource())) {
            System.out.println("Ignoring event from non-order source: " + orderEvent.getSource());
            return;
        }

        System.out.println("Received CREATE_ORDER event: " + orderEvent);

        PaymentRequestDto paymentRequest = new PaymentRequestDto();
        paymentRequest.setOrderId(orderEvent.getOrderId());
        paymentRequest.setUserId(orderEvent.getUserId());
        paymentRequest.setStatus(PaymentStatus.COMPLETED);
        paymentRequest.setTimestamp(orderEvent.getCreationDate().atStartOfDay());
        paymentRequest.setPaymentAmount(BigDecimal.ONE);

        paymentService.createPayment(paymentRequest);
    }
}