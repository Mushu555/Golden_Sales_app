
package com.goldensales.sales.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(
            @Qualifier("productRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public ProductDetails getProduct(Long productId) {
        try {
            ProductDetails product = restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductDetails.class);

            if (product == null
                    || product.id() == null
                    || product.price() == null
                    || product.name() == null) {
                throw new DownstreamServiceException(
                        "Product service returned incomplete product data");
            }

            if (!Boolean.TRUE.equals(product.active())) {
                throw new DownstreamServiceException(
                        "Product is not available for sale");
            }

            if (product.price().signum() < 0) {
                throw new DownstreamServiceException(
                        "Product service returned an invalid price");
            }

            return product;
        } catch (RestClientException exception) {
            throw new DownstreamServiceException(
                    "Unable to retrieve product details", exception);
        }
    }

    public record ProductDetails(
            Long id,
            String name,
            BigDecimal price,
            Boolean active) {
    }
}
