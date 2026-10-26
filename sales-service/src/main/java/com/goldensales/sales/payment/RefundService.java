
package com.goldensales.sales.payment;

import com.goldensales.sales.sale.Sale;
import com.goldensales.sales.sale.SaleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;

    public RefundService(
            RefundRepository refundRepository,
            PaymentRepository paymentRepository,
            SaleRepository saleRepository) {
        this.refundRepository = refundRepository;
        this.paymentRepository = paymentRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional
    public RefundResponse recordRefund(
            Long paymentId,
            RecordRefundRequest request,
            Jwt jwt) {

        requireAdmin(jwt);

        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payment not found"));

        Sale sale = saleRepository.findById(payment.getSaleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sale not found"));

        if (!"COMPLETED".equals(sale.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Refunds can only be recorded against completed sales");
        }

        BigDecimal amount;
        try {
            amount = request.amount().setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Refund amount must have at most two decimal places");
        }

        BigDecimal alreadyRefunded =
                refundRepository.getTotalRefundedForPayment(paymentId);

        BigDecimal refundable =
                payment.getAmount().subtract(alreadyRefunded);

        if (refundable.signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This payment has already been fully refunded");
        }

        if (amount.compareTo(refundable) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Refund exceeds the refundable balance of " + refundable);
        }

        Long refundedBy = getUserId(jwt);

        Refund refund = new Refund(
                payment.getId(),
                payment.getSaleId(),
                payment.getCustomerId(),
                amount,
                request.reference(),
                refundedBy);

        return RefundResponse.from(refundRepository.save(refund));
    }

    @Transactional(readOnly = true)
public java.util.List<RefundResponse> getRefundsByPayment(
        Long paymentId, Jwt jwt) {

    requireAdmin(jwt);

    return refundRepository.findByPaymentIdOrderByCreatedAtDesc(paymentId)
            .stream()
            .map(RefundResponse::from)
            .toList();
}

    private void requireAdmin(Jwt jwt) {
        if (!"ADMIN".equals(jwt.getClaimAsString("role"))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only admins can record refunds");
        }
    }

    private Long getUserId(Jwt jwt) {
        Number userId = jwt.getClaim("userId");

        if (userId == null || userId.longValue() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "JWT does not contain a valid user ID");
        }

        return userId.longValue();
    }
}
