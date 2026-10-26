
package com.goldensales.sales.payment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "sale_id", nullable = false)
    private Long saleId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 100)
    private String reference;

    @Column(name = "refunded_by", nullable = false)
    private Long refundedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Refund() {
    }

    public Refund(
            Long paymentId,
            Long saleId,
            Long customerId,
            BigDecimal amount,
            String reference,
            Long refundedBy) {
        this.paymentId = paymentId;
        this.saleId = saleId;
        this.customerId = customerId;
        this.amount = amount;
        this.reference = reference;
        this.refundedBy = refundedBy;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getSaleId() {
        return saleId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public Long getRefundedBy() {
        return refundedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
