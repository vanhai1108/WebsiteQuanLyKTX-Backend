package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Optional;

import com.ktx.quanlykytucxa.entities.Invoice;

public interface InvoiceService {
    List<Invoice> getAllInvoices();
    List<Invoice> getInvoicesByStudentId(Long studentId);
    Optional<Invoice> getInvoiceById(Long id);
    Invoice createInvoice(Invoice invoice, Long studentId);
    Invoice updateInvoiceStatus(Long id, String status);
    void deleteInvoice(Long id);
}
