
package com.goldensales.sales.sale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findBySaleNumber(String saleNumber);

    Page<Sale> findByCustomerId(Long customerId, Pageable pageable);
}
