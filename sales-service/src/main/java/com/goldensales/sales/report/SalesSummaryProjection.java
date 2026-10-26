
package com.goldensales.sales.report;

import java.math.BigDecimal;

public interface SalesSummaryProjection {
    Long getTotalSales();
    BigDecimal getTotalRevenue();
    BigDecimal getTotalDiscounts();
    BigDecimal getTotalTax();
}
