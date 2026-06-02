package com.ktx.quanlykytucxa.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ktx.quanlykytucxa.entities.Invoice;
import com.ktx.quanlykytucxa.entities.Notification;
import com.ktx.quanlykytucxa.repositories.NotificationRepository;
import com.ktx.quanlykytucxa.services.InvoiceService;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin("*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Invoice>> getInvoicesByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStudentId(studentId));
    }

    @PostMapping("/student/{studentId}")
    public ResponseEntity<?> createInvoice(@PathVariable Long studentId, @RequestBody Invoice invoice) {
        try {
            Invoice newInvoice = invoiceService.createInvoice(invoice, studentId);

            // Notify the specific student about their new invoice
            String desc = newInvoice.getDescription() != null ? newInvoice.getDescription() : "Hóa đơn phòng";
            String amountStr = newInvoice.getAmount() != null
                    ? String.format("%,.0f đ", newInvoice.getAmount())
                    : "—";
            Notification notif = Notification.builder()
                    .title("Hóa đơn mới: " + desc)
                    .content("Bạn có hóa đơn mới - " + desc + " với số tiền " + amountStr
                            + ". Vui lòng thanh toán đúng hạn.")
                    .createDate(LocalDateTime.now())
                    .targetRole("STUDENT")
                    .targetUserId(newInvoice.getStudent().getId())
                    .build();
            notificationRepository.save(notif);

            return ResponseEntity.status(HttpStatus.CREATED).body(newInvoice);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateInvoiceStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Invoice updated = invoiceService.updateInvoiceStatus(id, status);

            // Send specific notifications based on status
            if (updated.getStudent() != null) {
                String studentName = updated.getStudent().getFullName();
                String code = updated.getStudent().getStudentCode();
                String desc = updated.getDescription() != null ? updated.getDescription() : "Hóa đơn phòng";
                
                if ("PENDING".equalsIgnoreCase(status)) {
                    // Notify Admin that student has paid and is waiting for approval
                    Notification notif = Notification.builder()
                            .title("Yêu cầu duyệt hóa đơn: " + desc)
                            .content("Sinh viên " + studentName + " (" + code + ") đã báo cáo thanh toán hóa đơn: " + desc + ". Vui lòng kiểm tra và duyệt.")
                            .createDate(LocalDateTime.now())
                            .targetRole("ADMIN")
                            .targetUserId(null)
                            .build();
                    notificationRepository.save(notif);
                } else if ("PAID".equalsIgnoreCase(status)) {
                    // Notify Student that their payment has been approved
                    Notification notif = Notification.builder()
                            .title("Hóa đơn đã được duyệt: " + desc)
                            .content("Thanh toán cho hóa đơn " + desc + " của bạn đã được admin xác nhận thành công.")
                            .createDate(LocalDateTime.now())
                            .targetRole("STUDENT")
                            .targetUserId(updated.getStudent().getId())
                            .build();
                    notificationRepository.save(notif);
                } else if ("UNPAID".equalsIgnoreCase(status)) {
                    // Notify Student that their payment was rejected
                    Notification notif = Notification.builder()
                            .title("Yêu cầu thanh toán thất bại: " + desc)
                            .content("Yêu cầu thanh toán cho hóa đơn " + desc + " của bạn đã bị từ chối. Vui lòng thanh toán lại hoặc liên hệ quản lý.")
                            .createDate(LocalDateTime.now())
                            .targetRole("STUDENT")
                            .targetUserId(updated.getStudent().getId())
                            .build();
                    notificationRepository.save(notif);
                }
            }

            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
