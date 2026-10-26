
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
import java.util.Locale;
import java.util.Set;

@Service
public class PaymentService {

    private static final Set<String> PAYMENT_METHODS = Set.of(
            "CASH", "CARD", "UPI", "BANK_TRANSFER", "OTHER");

    private final PaymentRepository paymentRepository;
    private final SaleRepository saleRepository;
    private final RefundRepository refundRepository;

            PaymentRepository paymentRepository,
            SaleRepository saleRepository) {
        this.paymentRepository = paymentRepository;
        this.saleRepository = saleRepository;
        this.refundRepository = refundRepository;
    }

    @Transactional
    public PaymentResponse recordPayment(
            Long saleId,
            RecordPaymentRequest request,
            Jwt jwt) {

        requireAdmin(jwt);

        Sale sale = saleRepository.findByIdForUpdate(saleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sale not found"));

        if (!"COMPLETED".equals(sale.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payments cannot be recorded against a cancelled sale");
        }

        BigDecimal amount;
        try {
            amount = request.amount().setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment amount must have at most two decimal places");
        }

        String method = request.paymentMethod()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!PAYMENT_METHODS.contains(method)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment method must be CASH, CARD, UPI, BANK_TRANSFER, or OTHER");
        }

        BigDecimal totalPaid =
                paymentRepository.getTotalPaidForSale(saleId);

        BigDecimal outstanding =
                sale.getTotalAmount().subtract(totalPaid);

        if (outstanding.signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This sale has no outstanding balance");
        }

        if (amount.compareTo(outstanding) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment exceeds the outstanding balance of " + outstanding);
        }

        Payment payment = new Payment(
                sale.getId(),
                sale.getCustomerId(),
                amount,
                method,
                request.reference(),
                getUserId(jwt));

        return PaymentResponse.from(paymentRepository.save(payment));
    }


@Transactional(readOnly = true)
public java.util.List<PaymentResponse> getCustomerPayments(
        Long customerId, Jwt jwt) {

    requireAdmin(jwt);

    return paymentRepository
            .findByCustomerIdOrderByCreatedAtDesc(customerId)
            .stream()
            .map(PaymentResponse::from)
            .toList();
}


@Transactional(readOnly = true)
public CustomerDebtResponse getCustomerDebt(
        Long customerId, Jwt jwt) {

    requireAdmin(jwt);

    java.math.BigDecimal totalSales =
            saleRepository.findAll().stream()
                    .filter(sale ->
                            sale.getCustomerId().equals(customerId)
                            && "COMPLETED".equals(sale.getStatus()))
                    .map(com.goldensales.sales.sale.Sale::getTotalAmount)
                    .reduce(java.math.BigDecimal.ZERO,
                            java.math.BigDecimal::add);

    java.math.BigDecimal totalPayments =
            paymentRepository.getTotalPaidForCustomer(customerId);

    java.math.BigDecimal totalRefunds =
            refundRepository.getTotalRefundedForCustomer(customerId);

    java.math.BigDecimal netPaid =
            totalPayments.subtract(totalRefunds);

    java.math.BigDecimal outstanding =
            totalSales.subtract(netPaid);

    return new CustomerDebtResponse(
            customerId,
            totalSales,
            netPaid,
            outstanding);
}



@Transactional(readOnly = true)
public java.util.List<CustomerDebtResponse> getCustomersWithOutstandingDebt(
        Jwt jwt) {

    requireAdmin(jwt);

    return paymentRepository.findCustomersWithOutstandingDebt()
            .stream()
            .map(row -> new CustomerDebtResponse(
                    row.getCustomerId(),
                    row.getTotalSales(),
                    row.getTotalPaid(),
                    row.getOutstandingDebt()))
            .toList();
}



    private void requireAdmin(Jwt jwt) {
        if (!"ADMIN".equals(jwt.getClaimAsString("role"))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only admins can record customer payments");
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
