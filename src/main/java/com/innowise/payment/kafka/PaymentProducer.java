package com.innowise.payment.kafka;

import com.innowise.payment.dto.PaymentEventDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentEventDto> kafkaTemplate;

    public PaymentProducer(@Qualifier("paymentEventKafkaTemplate") KafkaTemplate<String,
            PaymentEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentEvent(String topic, PaymentEventDto event) {
        kafkaTemplate.send(topic, event);
    }
}