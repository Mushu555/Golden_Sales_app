
package com.goldensales.sales.payment;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long saleId,
        Long customerId,
        BigDecimal amount,
        String paymentMethod,
        String reference,
        Long receivedBy,
        Instant createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getSaleId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getReference(),
                payment.getReceivedBy(),
                payment.getCreatedAt()
        );
    }
}
