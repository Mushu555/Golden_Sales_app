
package com.goldensales.sales.sale;

import com.goldensales.sales.client.CustomerClient;
import com.goldensales.sales.client.DownstreamServiceException;
import com.goldensales.sales.client.ProductClient;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import com.goldensales.sales.payment.PaymentRepository;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.ZoneOffset;
@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductClient productClient;
    private final CustomerClient customerClient;
    private final PaymentRepository paymentRepository;
    public SaleService(
            SaleRepository saleRepository,
            ProductClient productClient,
            CustomerClient customerClient,
            PaymentRepository paymentRepository) {
        this.saleRepository = saleRepository;
        this.productClient = productClient;
        this.customerClient = customerClient;
    }

    @Transactional
    public SaleResponse createSale(CreateSaleRequest request, Jwt jwt) {
        Long userId = getUserId(jwt);

        try {
            customerClient.getCustomer(request.customerId());

            BigDecimal subtotal = BigDecimal.ZERO;

            Sale sale = new Sale(
                    generateSaleNumber(),
                    request.customerId(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    userId
            );

            for (CreateSaleRequest.SaleItemRequest itemRequest : request.items()) {
                ProductClient.ProductDetails product =
                        productClient.getProduct(itemRequest.productId());

                BigDecimal unitPrice = product.price()
                        .setScale(2, RoundingMode.UNNECESSARY);

                BigDecimal lineTotal = unitPrice.multiply(
                        BigDecimal.valueOf(itemRequest.quantity()));

                subtotal = subtotal.add(lineTotal);

                sale.addItem(new SaleItem(
                        product.id(),
                        product.name(),
                        itemRequest.quantity(),
                        unitPrice,
                        lineTotal
                ));
            }

            BigDecimal discount = BigDecimal.ZERO;
            BigDecimal tax = BigDecimal.ZERO;
            BigDecimal total = subtotal.subtract(discount).add(tax);

            Sale completedSale = new Sale(
                    sale.getSaleNumber(),
                    request.customerId(),
                    subtotal,
                    discount,
                    tax,
                    total,
                    userId
            );

            for (SaleItem item : sale.getItems()) {
                completedSale.addItem(new SaleItem(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                ));
            }

            return SaleResponse.from(saleRepository.save(completedSale));

        } catch (DownstreamServiceException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unable to validate customer or product details",
                    exception
            );
        } catch (ArithmeticException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product price must have at most two decimal places",
                    exception
            );
        }
    }

    
@Transactional(readOnly = true)
public Page<SaleResponse> getSales(
        Pageable pageable,
        Jwt jwt,
        LocalDate from,
        LocalDate to,
        String status) {

    if (from != null && to != null && from.isAfter(to)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "'from' date cannot be after 'to' date");
    }

    if (status != null
            && !status.equals("COMPLETED")
            && !status.equals("CANCELLED")) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Status must be COMPLETED or CANCELLED");
    }

    Specification<Sale> spec = (root, query, cb) -> cb.conjunction();

    if (from != null) {
        var start = from.atStartOfDay(ZoneOffset.UTC).toInstant();

        spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("createdAt"), start));
    }

    if (to != null) {
        var endExclusive = to.plusDays(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        spec = spec.and((root, query, cb) ->
                cb.lessThan(root.get("createdAt"), endExclusive));
    }

    if (status != null) {
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), status));
    }

    if (!isAdmin(jwt)) {
        Long userId = getUserId(jwt);

        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("createdBy"), userId));
    }

    return saleRepository.findAll(spec, pageable)
            .map(SaleResponse::from);
}


    @Transactional(readOnly = true)
    public Page<SaleResponse> getSales(Pageable pageable, Jwt jwt) {
        Page<Sale> sales = isAdmin(jwt)
                ? saleRepository.findAll(pageable)
                : saleRepository.findByCreatedBy(getUserId(jwt), pageable);

        return sales.map(SaleResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<SaleResponse> getSalesByCustomer(
            Long customerId,
            Pageable pageable,
            Jwt jwt) {

        Page<Sale> sales = isAdmin(jwt)
                ? saleRepository.findByCustomerId(customerId, pageable)
                : saleRepository.findByCustomerIdAndCreatedBy(
                        customerId, getUserId(jwt), pageable);

        return sales.map(SaleResponse::from);
    }



@Transactional
public SaleResponse cancelSale(Long id) {
    Sale sale = saleRepository.findByIdForUpdate(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Sale not found"));

    if ("CANCELLED".equals(sale.getStatus())) {
        return SaleResponse.from(sale);
    }

    if (paymentRepository.existsBySaleId(id)) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This sale has recorded payments. Process a refund before cancelling it.");
    }

    sale.cancel();

    return SaleResponse.from(saleRepository.save(sale));
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

    private boolean isAdmin(Jwt jwt) {
        return "ADMIN".equals(jwt.getClaimAsString("role"));
    }

    private ResponseStatusException saleNotFound() {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Sale not found");
    }

    private String generateSaleNumber() {
        return "SALE-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }
}
