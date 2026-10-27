package com.goldensales.sales.report;

import com.goldensales.sales.sale.Sale;
import com.goldensales.sales.sale.SaleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class SalesCsvExportService {

    private final SaleRepository saleRepository;

    public SalesCsvExportService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Transactional(readOnly = true)
    public byte[] exportSalesCsv() {
        StringBuilder csv = new StringBuilder();

        csv.append("Sale ID,Sale Number,Customer ID,Subtotal,Discount,Tax,Total,Status,Created By,Created At\n");

        int page = 0;
        Page<Sale> salesPage;

        do {
            salesPage = saleRepository.findAll(
                    PageRequest.of(page++, 500, Sort.by(Sort.Direction.DESC, "createdAt"))
            );

            for (Sale sale : salesPage.getContent()) {
                csv.append(sale.getId()).append(',')
                   .append(escape(sale.getSaleNumber())).append(',')
                   .append(sale.getCustomerId()).append(',')
                   .append(sale.getSubtotal()).append(',')
                   .append(sale.getDiscountAmount()).append(',')
                   .append(sale.getTaxAmount()).append(',')
                   .append(sale.getTotalAmount()).append(',')
                   .append(escape(sale.getStatus())).append(',')
                   .append(sale.getCreatedBy()).append(',')
                   .append(escape(String.valueOf(sale.getCreatedAt())))
                   .append('\n');
            }
        } while (salesPage.hasNext());

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}