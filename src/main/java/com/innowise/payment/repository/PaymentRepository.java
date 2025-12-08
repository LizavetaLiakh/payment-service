package com.innowise.payment.repository;

import com.innowise.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE payments SET order_id = :orderId, user_id = :userId, status = :status, " +
            "time_stamp = :timestamp, payment_amount = :paymentAmount WHERE id = :id", nativeQuery = true)
    int updatePayment(@Param("id") Long id, @Param("orderId") Long orderId, @Param("userId") Long userId,
                      @Param("status") String status, @Param("timestamp")LocalDateTime timestamp,
                      @Param("paymentAmount")BigDecimal paymentAmount);
}
