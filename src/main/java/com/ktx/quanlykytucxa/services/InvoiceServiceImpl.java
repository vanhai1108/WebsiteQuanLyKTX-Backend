package com.ktx.quanlykytucxa.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ktx.quanlykytucxa.entities.Invoice;
import com.ktx.quanlykytucxa.entities.InvoiceStatus;
import com.ktx.quanlykytucxa.entities.Student;
import com.ktx.quanlykytucxa.repositories.InvoiceRepository;
import com.ktx.quanlykytucxa.repositories.StudentRepository;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public List<Invoice> getInvoicesByStudentId(Long studentId) {
        return invoiceRepository.findByStudentId(studentId);
    }

    @Override
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public Invoice createInvoice(Invoice invoice, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        invoice.setStudent(student);
        if (invoice.getCreateDate() == null) {
            invoice.setCreateDate(LocalDate.now());
        }
        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.UNPAID);
        }

        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice updateInvoiceStatus(Long id, String statusStr) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        
        try {
            InvoiceStatus status = InvoiceStatus.valueOf(statusStr.toUpperCase());
            invoice.setStatus(status);
            return invoiceRepository.save(invoice);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái hóa đơn không hợp lệ");
        }
    }

    @Override
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        invoiceRepository.delete(invoice);
    }
}
