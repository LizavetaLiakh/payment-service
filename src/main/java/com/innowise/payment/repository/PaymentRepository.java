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
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId")
    List<Payment> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT p FROM Payment p WHERE p.userId = :userId")
    List<Payment> findByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Payment p WHERE p.status = :paymentStatus")
    List<Payment> findByPaymentStatus(@Param("paymentStatus") String paymentStatus);

    @Query("SELECT COALESCE(SUM(p.paymentAmount), 0) FROM Payment p WHERE p.timestamp " +
            "BETWEEN :beginningDate AND :endDate")
    BigDecimal getTotalPaymentsSumForDatePeriod(@Param("beginningDate") LocalDateTime beginningDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE payments SET order_id = :orderId, user_id = :userId, status = :paymentStatus, " +
            "time_stamp = :timestamp, payment_amount = :paymentAmount WHERE id = :id", nativeQuery = true)
    int updatePayment(@Param("id") Long id, @Param("orderId") Long orderId, @Param("userId") Long userId,
                      @Param("paymentStatus") String paymentStatus, @Param("timestamp") LocalDateTime timestamp,
                      @Param("paymentAmount") BigDecimal paymentAmount);
}
