
package com.goldensales.sales.payment;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/sales/{saleId}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse recordPayment(
            @PathVariable Long saleId,
            @Valid @RequestBody RecordPaymentRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return paymentService.recordPayment(saleId, request, jwt);
    }

    @GetMapping("/customers/{customerId}/payments")
    public List<PaymentResponse> getCustomerPayments(
            @PathVariable Long customerId,
            @AuthenticationPrincipal Jwt jwt) {
        return paymentService.getCustomerPayments(customerId, jwt);
    }

    @GetMapping("/sales/{saleId}/payments")
public java.util.List<PaymentResponse> getSalePayments(
        @PathVariable Long saleId,
        @AuthenticationPrincipal Jwt jwt) {

    return paymentService.getSalePayments(saleId, jwt);
}

    @GetMapping("/customers/{customerId}/debt")
    public CustomerDebtResponse getCustomerDebt(
            @PathVariable Long customerId,
            @AuthenticationPrincipal Jwt jwt) {
        return paymentService.getCustomerDebt(customerId, jwt);
    }


@GetMapping("/customers/debts")
public List<CustomerDebtResponse> getCustomersWithOutstandingDebt(
        @AuthenticationPrincipal Jwt jwt) {

    return paymentService.getCustomersWithOutstandingDebt(jwt);
}

}
