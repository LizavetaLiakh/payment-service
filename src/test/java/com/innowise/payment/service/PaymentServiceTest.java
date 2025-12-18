package com.innowise.payment.service;

import com.innowise.payment.dto.PaymentEventDto;
import com.innowise.payment.dto.PaymentRequestDto;
import com.innowise.payment.dto.PaymentResponseDto;
import com.innowise.payment.entity.Payment;
import com.innowise.payment.entity.PaymentStatus;
import com.innowise.payment.mapper.PaymentMapper;
import com.innowise.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;
    private PaymentRequestDto requestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        payment = new Payment();
        payment.setOrderId(10L);
        payment.setUserId(1L);
        payment.setPaymentAmount(BigDecimal.valueOf(100.0));
        payment.setTimestamp(LocalDateTime.of(2025, 12, 18, 12, 0));

        requestDto = new PaymentRequestDto();
        requestDto.setOrderId(10L);
        requestDto.setUserId(1L);
        requestDto.setPaymentAmount(BigDecimal.valueOf(100.0));
    }

    @Test
    void testCreatePaymentCompleted() {
        when(paymentMapper.toPayment(requestDto)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(1L);
            p.setStatus(PaymentStatus.COMPLETED);
            return p;
        });
        when(paymentMapper.toPaymentResponseDto(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            PaymentResponseDto dto = new PaymentResponseDto();
            dto.setId(p.getId());
            dto.setStatus(p.getStatus());
            dto.setOrderId(p.getOrderId());
            dto.setUserId(p.getUserId());
            dto.setPaymentAmount(p.getPaymentAmount());
            return dto;
        });

        PaymentResponseDto response = paymentService.createPayment(requestDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(PaymentStatus.COMPLETED, response.getStatus());

        verify(paymentRepository).save(any(Payment.class));
        verify(kafkaTemplate).send(eq("create_payment_v2"), any());
    }

    @Test
    void testCreatePaymentFailed() {
        when(paymentMapper.toPayment(requestDto)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(2L); // имитация присвоения id БД
            p.setStatus(PaymentStatus.FAILED);
            return p;
        });
        when(paymentMapper.toPaymentResponseDto(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            PaymentResponseDto dto = new PaymentResponseDto();
            dto.setId(p.getId());
            dto.setStatus(p.getStatus());
            dto.setOrderId(p.getOrderId());
            dto.setUserId(p.getUserId());
            dto.setPaymentAmount(p.getPaymentAmount());
            return dto;
        });

        PaymentResponseDto response = paymentService.createPayment(requestDto);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(PaymentStatus.FAILED, response.getStatus());

        verify(paymentRepository).save(any(Payment.class));
        verify(kafkaTemplate).send(eq("create_payment_v2"), any());
    }

    @Test
    void testKafkaEventSent() {
        when(paymentMapper.toPayment(requestDto)).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(3L);
            p.setStatus(PaymentStatus.COMPLETED);
            return p;
        });
        when(paymentMapper.toPaymentResponseDto(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            PaymentResponseDto dto = new PaymentResponseDto();
            dto.setId(p.getId());
            dto.setOrderId(p.getOrderId());
            dto.setUserId(p.getUserId());
            dto.setPaymentAmount(p.getPaymentAmount());
            dto.setStatus(p.getStatus());
            return dto;
        });

        PaymentResponseDto response = paymentService.createPayment(requestDto);

        assertNotNull(response);
        assertEquals(PaymentStatus.COMPLETED, response.getStatus());

        verify(kafkaTemplate, times(1)).send(eq("create_payment_v2"), argThat(event ->
                event instanceof PaymentEventDto &&
                        ((PaymentEventDto) event).getOrderId().equals(payment.getOrderId()) &&
                        ((PaymentEventDto) event).getUserId().equals(payment.getUserId()) &&
                        ((PaymentEventDto) event).getStatus() == PaymentStatus.COMPLETED
        ));
    }
}