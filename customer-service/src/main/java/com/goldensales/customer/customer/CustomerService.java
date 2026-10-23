package com.goldensales.customer.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Page<CustomerResponse> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return customerRepository.findByActiveTrue(pageable).map(CustomerResponse::from);
        }
        String term = query.trim();
        return customerRepository.findByActiveTrueAndNameContainingIgnoreCase(term, pageable)
                .map(CustomerResponse::from);
    }

    public CustomerResponse getById(Long id) {
        return CustomerResponse.from(findActive(id));
    }

    public CustomerResponse getByPhone(String phone) {
        String normalized = normalizePhone(phone);
        Customer customer = customerRepository.findByPhoneAndActiveTrue(normalized)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return CustomerResponse.from(customer);
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        Customer customer = new Customer();
        apply(customer, request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = findActive(id);
        apply(customer, request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public void deactivate(Long id) {
        Customer customer = findActive(id);
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private Customer findActive(Long id) {
        return customerRepository.findById(id).filter(Customer::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
    }

    private void apply(Customer customer, CustomerRequest request) {
        customer.setName(request.name().trim());
        customer.setPhone(normalizePhone(request.phone()));
        customer.setAddress(request.address() == null ? null : request.address().trim());
    }

    private String normalizePhone(String phone) {
        return phone.replaceAll("[\s().-]", "");
    }
}
