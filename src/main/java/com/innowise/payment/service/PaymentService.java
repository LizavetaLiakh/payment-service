package com.innowise.payment.service;

import com.innowise.payment.dto.PaymentRequestDto;
import com.innowise.payment.dto.PaymentResponseDto;
import com.innowise.payment.entity.Payment;
import com.innowise.payment.entity.PaymentStatus;
import com.innowise.payment.mapper.PaymentMapper;
import com.innowise.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Random;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final WebClient webClient = WebClient.create("https://www.randomnumberapi.com/api/v1.0/random");

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    public PaymentResponseDto createPayment(PaymentRequestDto dto) {
        Payment payment = paymentMapper.toPayment(dto);

        Integer randomNumber = getRandomNumber();

        if (randomNumber % 2 == 0) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toPaymentResponseDto(saved);
    }

    private Integer getRandomNumber() {
        try {
            Integer[] numbers = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("min", 1)
                            .queryParam("max", 100)
                            .queryParam("count", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(Integer[].class)
                    .block();

            return numbers != null && numbers.length > 0 ? numbers[0] : new Random().nextInt(100);
        } catch (Exception e) {
            return new Random().nextInt(100);
        }
    }
}