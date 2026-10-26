
package com.goldensales.sales.sale;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SaleResponse(
        Long id,
        String saleNumber,
        Long customerId,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String status,
        Long createdBy,
        Instant createdAt,
        List<SaleItemResponse> items
) {
    public static SaleResponse from(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getSaleNumber(),
                sale.getCustomerId(),
                sale.getSubtotal(),
                sale.getDiscountAmount(),
                sale.getTaxAmount(),
                sale.getTotalAmount(),
                sale.getStatus(),
                sale.getCreatedBy(),
                sale.getCreatedAt(),
                sale.getItems().stream()
                        .map(SaleItemResponse::from)
                        .toList()
        );
    }
}
