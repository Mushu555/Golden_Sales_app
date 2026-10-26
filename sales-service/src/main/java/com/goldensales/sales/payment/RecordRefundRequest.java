
package com.goldensales.sales.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record RecordRefundRequest(
        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @Size(max = 100)
        String reference
) {}
