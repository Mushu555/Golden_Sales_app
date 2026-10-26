
package com.goldensales.sales.payment;

import java.math.BigDecimal;

public record CustomerDebtResponse(
        Long customerId,
        BigDecimal totalCompletedSales,
        BigDecimal totalPaid,
        BigDecimal outstandingDebt
) {}
