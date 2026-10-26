
package com.goldensales.sales.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySalesResponse(
        LocalDate date,
        long totalSales,
        BigDecimal revenue
) {}
