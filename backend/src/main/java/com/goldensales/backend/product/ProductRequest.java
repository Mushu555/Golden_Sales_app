package com.goldensales.backend.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 255) String description,
        @NotNull @DecimalMin(value = "0.01", message = "must be greater than zero") BigDecimal price,
        @PositiveOrZero int stockQuantity
) {}
