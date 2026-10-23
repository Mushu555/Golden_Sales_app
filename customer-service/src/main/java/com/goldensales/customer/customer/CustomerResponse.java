package com.goldensales.customer.customer;

public record CustomerResponse(Long id, String name, String phone, String address, boolean active) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getPhone(),
                customer.getAddress(), customer.isActive());
    }
}
