
package com.goldensales.sales.report;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.goldensales.sales.sale.SaleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales/reports")
public class SalesReportController {

    private final SalesReportService reportService;
    private final SalesCsvExportService;

    public SalesReportController(SalesReportService reportService) {
        this.reportService = reportService;
        this.SalesCsvExportService=salesCsvExportService;
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

    @GetMapping("/audit")
public Page<SalesAuditResponse> getSalesAudit(
        @PageableDefault(size = 20, sort = "createdAt")
        Pageable pageable) {

    return saleRepository.findSalesAudit(pageable);
}


@GetMapping(value = "/export", produces = "text/csv")
public org.springframework.http.ResponseEntity<byte[]> exportSalesCsv() {
    byte[] csv = salesCsvExportService.exportSalesCsv();

    return org.springframework.http.ResponseEntity.ok()
            .header(
                    org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"sales-report.csv\"")
            .body(csv);
}

    @GetMapping(value = "/export", produces = "text/csv")
public org.springframework.http.ResponseEntity<byte[]> exportSalesCsv() {
    byte[] csv = salesCsvExportService.exportSalesCsv();

    return org.springframework.http.ResponseEntity.ok()
            .header(
                    org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"sales-report.csv\"")
            .body(csv);
}
}
