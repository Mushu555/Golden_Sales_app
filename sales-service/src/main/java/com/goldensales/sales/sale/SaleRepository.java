
package com.goldensales.sales.sale;

import com.goldensales.sales.report.DailySalesProjection;
import com.goldensales.sales.report.SalesSummaryProjection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
public interface SaleRepository extends
        JpaRepository<Sale, Long>,
        JpaSpecificationExecutor<Sale> {

    Optional<Sale> findBySaleNumber(String saleNumber);

    Page<Sale> findByCustomerId(Long customerId, Pageable pageable);

    Page<Sale> findByCreatedBy(Long createdBy, Pageable pageable);

    Page<Sale> findByCustomerIdAndCreatedBy(
            Long customerId, Long createdBy, Pageable pageable);

    Optional<Sale> findByIdAndCreatedBy(Long id, Long createdBy);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM Sale s WHERE s.id = :id")
Optional<Sale> findByIdForUpdate(@Param("id") Long id);

    @Query(value = """
            SELECT
                COUNT(*) AS "totalSales",
                COALESCE(SUM(total_amount), 0) AS "totalRevenue",
                COALESCE(SUM(discount_amount), 0) AS "totalDiscounts",
                COALESCE(SUM(tax_amount), 0) AS "totalTax"
            FROM sales
            WHERE status = 'COMPLETED'
              AND (:fromDate IS NULL OR created_at >= :fromDate)
              AND (:toDate IS NULL OR created_at < :toDate)
              AND (:isAdmin = TRUE OR created_by = :userId)
            """, nativeQuery = true)
    SalesSummaryProjection getSalesSummary(
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            @Param("isAdmin") boolean isAdmin,
            @Param("userId") Long userId);



            @Query("""
        SELECT new com.goldensales.sales.report.SalesAuditResponse(
            s.id,
            s.saleNumber,
            s.customerId,
            s.totalAmount,
            s.status,
            s.createdBy,
            s.createdAt
        )
        FROM Sale s
        ORDER BY s.createdAt DESC
        """)
Page<SalesAuditResponse> findSalesAudit(Pageable pageable);

    @Query(value = """
            SELECT
                CAST(created_at AT TIME ZONE 'UTC' AS date) AS "date",
                COUNT(*) AS "totalSales",
                COALESCE(SUM(total_amount), 0) AS "revenue"
            FROM sales
            WHERE status = 'COMPLETED'
              AND (:fromDate IS NULL OR created_at >= :fromDate)
              AND (:toDate IS NULL OR created_at < :toDate)
              AND (:isAdmin = TRUE OR created_by = :userId)
            GROUP BY CAST(created_at AT TIME ZONE 'UTC' AS date)
            ORDER BY CAST(created_at AT TIME ZONE 'UTC' AS date)
            """, nativeQuery = true)
    java.util.List<DailySalesProjection> getDailySales(
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            @Param("isAdmin") boolean isAdmin,
            @Param("userId") Long userId);
}
