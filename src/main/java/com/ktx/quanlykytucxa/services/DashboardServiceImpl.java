package com.ktx.quanlykytucxa.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ktx.quanlykytucxa.dto.AdminDashboardResponse;
import com.ktx.quanlykytucxa.dto.StudentDashboardResponse;
import com.ktx.quanlykytucxa.entities.Student;
import com.ktx.quanlykytucxa.repositories.ContractRepository;
import com.ktx.quanlykytucxa.repositories.InvoiceRepository;
import com.ktx.quanlykytucxa.repositories.RegistrationRepository;
import com.ktx.quanlykytucxa.repositories.RoomRepository;
import com.ktx.quanlykytucxa.repositories.StudentRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final InvoiceRepository invoiceRepository;
    private final ContractRepository contractRepository;
    private final RegistrationRepository registrationRepository;

    public DashboardServiceImpl(StudentRepository studentRepository, RoomRepository roomRepository, InvoiceRepository invoiceRepository, ContractRepository contractRepository, RegistrationRepository registrationRepository) {
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.invoiceRepository = invoiceRepository;
        this.contractRepository = contractRepository;
        this.registrationRepository = registrationRepository;
    }

    @Override
    public AdminDashboardResponse getAdminDashboardMetrics() {
        long totalStudents = studentRepository.count();
        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.countAvailableRooms();

        return AdminDashboardResponse.builder()
                .totalStudents(totalStudents)
                .totalRooms(totalRooms)
                .availableRooms(availableRooms)
                .monthlyRevenue(0.0)
                .pendingRegistrations(0)
                .recentPending(java.util.Collections.emptyList())
                .build();
    }

    @Override
    public StudentDashboardResponse getStudentDashboardMetrics(Long studentId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        
        if (studentOpt.isEmpty()) {
            return StudentDashboardResponse.builder()
                .studentName("Không tìm thấy")
                .roomCode("Chưa xếp")
                .building("-")
                .currentDebt(0)
                .activeContracts(0)
                .registrationStatus("NONE")
                .build();
        }

        Student student = studentOpt.get();
        String roomCode = student.getRoom() != null ? student.getRoom().getRoomCode() : "Chưa có";
        String building = student.getRoom() != null ? student.getRoom().getBuilding() : "-";

        return StudentDashboardResponse.builder()
                .studentName(student.getFullName())
                .roomCode(roomCode)
                .building(building)
                .currentDebt(0.0)
                .activeContracts(0)
                .registrationStatus(student.getRoom() != null ? "APPROVED" : "NONE")
                .build();
    }
}
