package com.goldensales.customer.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 255)
        @Pattern(regexp = "[+0-9() .-]{7,255}", message = "must be a valid phone number") String phone,
        @Size(max = 255) String address
) {}
