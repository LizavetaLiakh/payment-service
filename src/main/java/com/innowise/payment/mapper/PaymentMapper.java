package com.innowise.payment.mapper;

import com.innowise.payment.dto.PaymentRequestDto;
import com.innowise.payment.dto.PaymentResponseDto;
import com.innowise.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper for converting between Payment entity, PaymentRequestDto and PaymentResponseDto.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    /**
     * Maps Payment entity to PaymentResponseDto.
     * @param payment entity object that needs to be mapped
     * @return PaymentResponseDto object
     */
    PaymentResponseDto toPaymentResponseDto(Payment payment);

    /**
     * Maps PaymentRequestDto to Payment entity.
     * @param paymentRequestDto DTO object that needs to be mapped
     * @return Payment entity
     */
    Payment toPayment(PaymentRequestDto paymentRequestDto);
}
