package com.goldensales.backend.product;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price,
                              int stockQuantity, boolean active) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getStockQuantity(), product.isActive());
    }
}
