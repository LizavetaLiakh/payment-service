package com.innowise.payment.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.innowise.payment.dto.PaymentRequestDto;
import com.innowise.payment.dto.PaymentResponseDto;
import com.innowise.payment.entity.PaymentStatus;
import com.innowise.payment.repository.PaymentRepository;
import com.innowise.payment.service.PaymentService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaymentIntegrationTest {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
    static final KafkaContainer kafka = new KafkaContainer();

    static {
        postgres.start();
        kafka.start();

        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
        System.setProperty("random.api.base-url", "http://localhost:8089");
    }

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    private Consumer<String, Object> consumer;
    private static final WireMockServer wireMockServer = new WireMockServer(options().port(8089));

    @BeforeAll
    void setup() {
        wireMockServer.start();
        wireMockServer.stubFor(get(urlPathEqualTo("/api/v1.0/random"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[2]")));

        consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList("create_payment_v2"));
    }

    @AfterAll
    void teardown() {
        if (consumer != null) consumer.close();
        wireMockServer.stop();
        kafka.stop();
        postgres.stop();
    }

    @BeforeEach
    void cleanup() {
        paymentRepository.deleteAll();
        wireMockServer.resetRequests();
    }

    @Test
    void testPaymentCreation() {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setUserId(1L);
        dto.setOrderId(100L);
        dto.setPaymentAmount(BigDecimal.valueOf(500));
        dto.setTimestamp(LocalDateTime.now());

        PaymentResponseDto response = paymentService.createPayment(dto);

        assertNotNull(response.getId());
        assertTrue(response.getStatus() == PaymentStatus.COMPLETED
                || response.getStatus() == PaymentStatus.FAILED);
        assertTrue(paymentRepository.existsById(response.getId()));

        ConsumerRecords<String, Object> records = consumer.poll(java.time.Duration.ofSeconds(5));
        assertFalse(records.isEmpty(), "No Kafka message received");
    }
}