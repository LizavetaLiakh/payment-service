package com.innowise.payment.integration;

import com.innowise.payment.dto.OrderEventDto;
import com.innowise.payment.dto.PaymentRequestDto;
import com.innowise.payment.dto.PaymentResponseDto;
import com.innowise.payment.entity.PaymentStatus;
import com.innowise.payment.repository.PaymentRepository;
import com.innowise.payment.service.PaymentService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentIntegrationTest extends AbstractPaymentIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ConsumerFactory<String, OrderEventDto> consumerFactory;

    private Consumer<String, OrderEventDto> consumer;

    @BeforeAll
    void setup() {
        startWireMock(); // Убедимся, что WireMock запущен
        consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList("create_payment_v2"));
    }

    @AfterAll
    void teardown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @BeforeEach
    void cleanup() {
        paymentRepository.deleteAll();
        wireMockServer.resetAll();
    }

    @Test
    void testPaymentIsSavedAndKafkaEventSent() {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setUserId(1L);
        dto.setOrderId(50L);
        dto.setPaymentAmount(BigDecimal.valueOf(250.00));
        dto.setTimestamp(LocalDateTime.now());
        dto.setStatus(PaymentStatus.COMPLETED);

        PaymentResponseDto response = paymentService.createPayment(dto);

        // Проверяем сохранение
        assertNotNull(response.getId());
        assertEquals(PaymentStatus.COMPLETED, response.getStatus());
        assertTrue(paymentRepository.existsById(response.getId()));

        // Проверяем Kafka
        ConsumerRecords<String, OrderEventDto> records = consumer.poll(Duration.ofSeconds(5));
        assertFalse(records.isEmpty(), "No Kafka message received");

        ConsumerRecord<String, OrderEventDto> record = records.iterator().next();
        assertEquals(50L, record.value().getOrderId());
        assertEquals("payment-service", record.value().getSource());
    }
}
