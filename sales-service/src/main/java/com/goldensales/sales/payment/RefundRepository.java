
package com.goldensales.sales.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    List<Refund> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Refund> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM Refund r
            WHERE r.paymentId = :paymentId
            """)
    BigDecimal getTotalRefundedForPayment(
            @Param("paymentId") Long paymentId);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM Refund r
            WHERE r.customerId = :customerId
            """)
    BigDecimal getTotalRefundedForCustomer(
            @Param("customerId") Long customerId);
}
