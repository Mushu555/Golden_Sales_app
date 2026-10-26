
package com.goldensales.sales.payment;

import java.math.BigDecimal;

public interface CustomerDebtListProjection {
    Long getCustomerId();
    BigDecimal getTotalSales();
    BigDecimal getTotalPaid();
    BigDecimal getOutstandingDebt();
}
