package com.goldensales.sales.report;

import java.math.BigDecimal;
import java.time.Instant;

public record SalesAuditResponse(
        Long saleId,
        String saleNumber,
        Long customerId,
        BigDecimal totalAmount,
        String status,
        Long createdBy,
        Instant createdAt
) {}