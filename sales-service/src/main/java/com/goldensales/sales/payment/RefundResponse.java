
package com.goldensales.sales.payment;

import java.math.BigDecimal;
import java.time.Instant;

public record RefundResponse(
        Long id,
        Long paymentId,
        Long saleId,
        Long customerId,
        BigDecimal amount,
        String reference,
        Long refundedBy,
        Instant createdAt
) {
    public static RefundResponse from(Refund refund) {
        return new RefundResponse(
                refund.getId(),
                refund.getPaymentId(),
                refund.getSaleId(),
                refund.getCustomerId(),
                refund.getAmount(),
                refund.getReference(),
                refund.getRefundedBy(),
                refund.getCreatedAt()
        );
    }
}
