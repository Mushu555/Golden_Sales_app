
package com.goldensales.sales.report;

import com.goldensales.sales.sale.SaleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class SalesReportService {

    private final SaleRepository saleRepository;

    public SalesReportService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Transactional(readOnly = true)
    public SalesSummaryResponse getSummary(
            LocalDate from,
            LocalDate to,
            Jwt jwt) {

        validateDates(from, to);

        SalesSummaryProjection result = saleRepository.getSalesSummary(
                toInstant(from),
                toExclusiveInstant(to),
                isAdmin(jwt),
                getUserId(jwt));

        return new SalesSummaryResponse(
                result.getTotalSales(),
                result.getTotalRevenue(),
                result.getTotalDiscounts(),
                result.getTotalTax());
    }

    @Transactional(readOnly = true)
    public List<DailySalesResponse> getDailySales(
            LocalDate from,
            LocalDate to,
            Jwt jwt) {

        validateDates(from, to);

        return saleRepository.getDailySales(
                        toInstant(from),
                        toExclusiveInstant(to),
                        isAdmin(jwt),
                        getUserId(jwt))
                .stream()
                .map(row -> new DailySalesResponse(
                        row.getDate(),
                        row.getTotalSales(),
                        row.getRevenue()))
                .toList();
    }

    private void validateDates(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'from' date cannot be after 'to' date");
        }
    }

    private Instant toInstant(LocalDate date) {
        return date == null
                ? null
                : date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private Instant toExclusiveInstant(LocalDate to) {
        return to == null
                ? null
                : to.plusDays(1)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();
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
}
