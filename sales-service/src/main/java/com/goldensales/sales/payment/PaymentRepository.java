
package com.goldensales.sales.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySaleIdOrderByCreatedAtDesc(Long saleId);

    List<Payment> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Payment p WHERE p.id = :id")
Optional<Payment> findByIdForUpdate(@Param("id") Long id);


    List<Payment> findByCustomerIdAndSaleIdOrderByCreatedAtDesc(
        Long customerId, Long saleId);

    
        boolean existsBySaleId(Long saleId);




    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.saleId = :saleId
            """)
    BigDecimal getTotalPaidForSale(@Param("saleId") Long saleId);

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        JOIN Sale s ON s.id = p.saleId
        WHERE p.customerId = :customerId
          AND s.status = 'COMPLETED'
        """)
    BigDecimal getTotalPaidForCustomer(
            @Param("customerId") Long customerId);

    

@Query(value = """
        WITH sales_totals AS (
            SELECT customer_id, SUM(total_amount) AS total_sales
            FROM sales
            WHERE status = 'COMPLETED'
            GROUP BY customer_id
        ),
        payment_totals AS (
            SELECT p.customer_id, SUM(p.amount) AS gross_paid
            FROM payments p
            JOIN sales s ON s.id = p.sale_id
            WHERE s.status = 'COMPLETED'
            GROUP BY p.customer_id
        ),
        refund_totals AS (
            SELECT r.customer_id, SUM(r.amount) AS total_refunded
            FROM refunds r
            JOIN sales s ON s.id = r.sale_id
            WHERE s.status = 'COMPLETED'
            GROUP BY r.customer_id
        )
        SELECT
            st.customer_id AS "customerId",
            st.total_sales AS "totalSales",
            COALESCE(pt.gross_paid, 0)
                - COALESCE(rt.total_refunded, 0) AS "totalPaid",
            st.total_sales
                - COALESCE(pt.gross_paid, 0)
                + COALESCE(rt.total_refunded, 0)
                AS "outstandingDebt"
        FROM sales_totals st
        LEFT JOIN payment_totals pt
            ON pt.customer_id = st.customer_id
        LEFT JOIN refund_totals rt
            ON rt.customer_id = st.customer_id
        WHERE st.total_sales
                - COALESCE(pt.gross_paid, 0)
                + COALESCE(rt.total_refunded, 0) > 0
        ORDER BY "outstandingDebt" DESC
        """, nativeQuery = true)
List<CustomerDebtListProjection> findCustomersWithOutstandingDebt();


}
