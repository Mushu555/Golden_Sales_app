
package com.goldensales.sales.report;

import java.math.BigDecimal;

public record SalesSummaryResponse(
        long totalSales,
        BigDecimal totalRevenue,
        BigDecimal totalDiscounts,
        BigDecimal totalTax
) {}
