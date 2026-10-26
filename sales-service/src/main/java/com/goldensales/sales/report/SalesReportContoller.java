
package com.goldensales.sales.report;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales/reports")
public class SalesReportController {

    private final SalesReportService reportService;

    public SalesReportController(SalesReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public SalesSummaryResponse getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @AuthenticationPrincipal Jwt jwt) {

        return reportService.getSummary(from, to, jwt);
    }

    @GetMapping("/daily")
    public List<DailySalesResponse> getDailySales(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @AuthenticationPrincipal Jwt jwt) {

        return reportService.getDailySales(from, to, jwt);
    }
}
