
package com.goldensales.sales.report;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailySalesProjection {
    LocalDate getDate();
    Long getTotalSales();
    BigDecimal getRevenue();
}
