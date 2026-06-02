package com.ktx.quanlykytucxa.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ktx.quanlykytucxa.entities.InvoiceStatus;
import com.ktx.quanlykytucxa.entities.RegistrationStatus;
import com.ktx.quanlykytucxa.repositories.InvoiceRepository;
import com.ktx.quanlykytucxa.repositories.RegistrationRepository;
import com.ktx.quanlykytucxa.repositories.RoomRepository;
import com.ktx.quanlykytucxa.repositories.StudentRepository;

@Service
public class ReportService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();

        // Room Stats
        long totalRooms = roomRepository.count();
        long totalCapacity = roomRepository.findAll().stream().mapToLong(r -> r.getMaxCapacity()).sum();
        long currentOccupancy = roomRepository.findAll().stream().mapToLong(r -> r.getCurrentCapacity()).sum();
        
        stats.put("totalRooms", totalRooms);
        stats.put("totalCapacity", totalCapacity);
        stats.put("currentOccupancy", currentOccupancy);
        stats.put("occupancyRate", totalCapacity > 0 ? (double) currentOccupancy / totalCapacity * 100 : 0);

        // Student Stats
        stats.put("totalStudents", studentRepository.count());

        // Invoice/Revenue Stats
        double totalRevenue = invoiceRepository.findAll().stream()
                .filter(i -> InvoiceStatus.PAID.equals(i.getStatus()))
                .mapToDouble(i -> i.getAmount())
                .sum();
        stats.put("totalRevenue", totalRevenue);
        stats.put("pendingInvoices", invoiceRepository.findAll().stream().filter(i -> InvoiceStatus.UNPAID.equals(i.getStatus())).count());

        // Registration Stats
        stats.put("pendingRegistrations", registrationRepository.findAll().stream().filter(r -> RegistrationStatus.PENDING.equals(r.getStatus())).count());
        stats.put("approvedRegistrations", registrationRepository.findAll().stream().filter(r -> RegistrationStatus.APPROVED.equals(r.getStatus())).count());
        stats.put("rejectedRegistrations", registrationRepository.findAll().stream().filter(r -> RegistrationStatus.REJECTED.equals(r.getStatus())).count());

        return stats;
    }
}
