package com.goldensales.customer;

import com.goldensales.customer.customer.Customer;
import com.goldensales.customer.customer.CustomerRepository;
import com.goldensales.customer.customer.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceApplicationTests {
    @Test
    void searchReturnsMappedActiveCustomers() {
        CustomerRepository repository = mock(CustomerRepository.class);
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setName("Ahmed Stores");
        customer.setPhone("+919876543210");
        when(repository.findByActiveTrue(PageRequest.of(0, 20))).thenReturn(new PageImpl<>(List.of(customer)));

        CustomerService service = new CustomerService(repository);
        var result = service.search(null, PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals("Ahmed Stores", result.getContent().getFirst().name());
        verify(repository).findByActiveTrue(PageRequest.of(0, 20));
    }

    @Test
    void phoneLookupNormalizesPunctuation() {
        CustomerRepository repository = mock(CustomerRepository.class);
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setName("Ahmed Stores");
        customer.setPhone("+919876543210");
        when(repository.findByPhoneAndActiveTrue("+919876543210")).thenReturn(Optional.of(customer));

        CustomerService service = new CustomerService(repository);
        var result = service.getByPhone("+91 98765-43210");

        assertEquals("+919876543210", result.phone());
    }
}
