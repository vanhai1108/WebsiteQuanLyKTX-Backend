package com.ktx.quanlykytucxa.repositories;

import com.ktx.quanlykytucxa.entities.Invoice;
import com.ktx.quanlykytucxa.entities.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStudentId(Long studentId);
    List<Invoice> findByStatus(InvoiceStatus status);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Invoice i WHERE i.status = :status")
    Double sumAmountByStatus(@Param("status") InvoiceStatus status);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Invoice i WHERE i.student.id = :studentId AND i.status = :status")
    Double sumDebtByStudent(@Param("studentId") Long studentId, @Param("status") InvoiceStatus status);
}
