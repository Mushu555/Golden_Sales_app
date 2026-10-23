package com.goldensales.customer.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhoneAndActiveTrue(String phone);
    Page<Customer> findByActiveTrue(Pageable pageable);
    Page<Customer> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Customer> findByActiveTrueAndPhoneContaining(String phone, Pageable pageable);
}
