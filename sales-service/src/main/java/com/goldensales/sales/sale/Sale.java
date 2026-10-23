
package com.goldensales.sales.sale;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sale_number", nullable = false, unique = true, length = 40)
    private String saleNumber;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false, length = 20)
    private String status = "COMPLETED";

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(
            mappedBy = "sale",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SaleItem> items = new ArrayList<>();

    protected Sale() {}

    public Sale(
            String saleNumber,
            Long customerId,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal taxAmount,
            BigDecimal totalAmount,
            Long createdBy) {
        this.saleNumber = saleNumber;
        this.customerId = customerId;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.createdBy = createdBy;
        this.status = "COMPLETED";
    }

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public void addItem(SaleItem item) {
        items.add(item);
        item.setSale(this);
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    public Long getId() { return id; }
    public String getSaleNumber() { return saleNumber; }
    public Long getCustomerId() { return customerId; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Long getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public List<SaleItem> getItems() { return List.copyOf(items); }
}
