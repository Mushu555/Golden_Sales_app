
package com.goldensales.sales.sale;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleResponse createSale(
            @Valid @RequestBody CreateSaleRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return saleService.createSale(request, jwt);
    }

    @GetMapping("/{id}")
    public SaleResponse getSale(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return saleService.getSale(id, jwt);
    }

    
@GetMapping
public Page<SaleResponse> getSales(
        @PageableDefault(size = 20, sort = "createdAt")
        Pageable pageable,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,

        @RequestParam(required = false)
        String status,

        @AuthenticationPrincipal Jwt jwt) {

    return saleService.getSales(pageable, jwt, from, to, status);
}


    @GetMapping("/customer/{customerId}")
    public Page<SaleResponse> getSalesByCustomer(
            @PathVariable Long customerId,
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        return saleService.getSalesByCustomer(customerId, pageable, jwt);
    }


@PatchMapping("/{id}/cancel")
public SaleResponse cancelSale(@PathVariable Long id) {
    return saleService.cancelSale(id);
}

}
