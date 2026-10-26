
package com.goldensales.sales.payment;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class RefundController {

    private final RefundService refundService;

    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/{paymentId}/refunds")
    @ResponseStatus(HttpStatus.CREATED)
    public RefundResponse recordRefund(
            @PathVariable Long paymentId,
            @Valid @RequestBody RecordRefundRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return refundService.recordRefund(paymentId, request, jwt);
    }

    @GetMapping("/{paymentId}/refunds")
public java.util.List<RefundResponse> getRefunds(
        @PathVariable Long paymentId,
        @org.springframework.security.core.annotation.AuthenticationPrincipal
        org.springframework.security.oauth2.jwt.Jwt jwt) {

    return refundService.getRefundsByPayment(paymentId, jwt);
}
}
