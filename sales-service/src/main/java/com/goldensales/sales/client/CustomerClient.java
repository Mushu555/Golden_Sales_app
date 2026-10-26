
package com.goldensales.sales.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class CustomerClient {

    private final RestClient restClient;

    public CustomerClient(
            @Qualifier("customerRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public CustomerDetails getCustomer(Long customerId) {
        try {
            CustomerDetails customer = restClient.get()
                    .uri("/api/customers/{id}", customerId)
                    .retrieve()
                    .body(CustomerDetails.class);

            if (customer == null || customer.id() == null) {
                throw new DownstreamServiceException(
                        "Customer service returned incomplete customer data");
            }

            if (Boolean.FALSE.equals(customer.active())) {
                throw new DownstreamServiceException(
                        "Customer is inactive");
            }

            return customer;
        } catch (RestClientException exception) {
            throw new DownstreamServiceException(
                    "Unable to retrieve customer details", exception);
        }
    }

    public record CustomerDetails(
            Long id,
            String name,
            Boolean active) {
    }
}
