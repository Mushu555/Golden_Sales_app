
package com.goldensales.sales.sale;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateSaleRequest(
        @NotNull @Positive Long customerId,
        @NotEmpty List<@Valid SaleItemRequest> items
) {
    public record SaleItemRequest(
            @NotNull @Positive Long productId,
            @NotNull @Positive Integer quantity
    ) {}
}
