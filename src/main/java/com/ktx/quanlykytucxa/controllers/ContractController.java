package com.ktx.quanlykytucxa.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.ktx.quanlykytucxa.entities.Contract;
import com.ktx.quanlykytucxa.entities.Notification;
import com.ktx.quanlykytucxa.entities.RegistrationStatus;
import com.ktx.quanlykytucxa.entities.Room;
import com.ktx.quanlykytucxa.entities.Student;
import java.time.LocalDateTime;
import com.ktx.quanlykytucxa.repositories.ContractRepository;
import com.ktx.quanlykytucxa.repositories.RegistrationRepository;
import com.ktx.quanlykytucxa.repositories.RoomRepository;
import com.ktx.quanlykytucxa.repositories.StudentRepository;
import com.ktx.quanlykytucxa.services.ContractPdfService;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin("*")
public class ContractController {

    @Autowired private ContractRepository contractRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private com.ktx.quanlykytucxa.repositories.NotificationRepository notificationRepository;
    @Autowired private ContractPdfService contractPdfService;

    @GetMapping
    public ResponseEntity<List<Contract>> getAll() {
        return ResponseEntity.ok(contractRepository.findAll());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Contract>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(contractRepository.findByStudentId(studentId));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createContract(@RequestBody Map<String, Object> body) {
        try {
            Long studentId = Long.valueOf(body.get("studentId").toString());
            Long roomId    = Long.valueOf(body.get("roomId").toString());
            Long registrationId = body.get("registrationId") != null ? Long.valueOf(body.get("registrationId").toString()) : null;
            Double amount = body.get("amount") != null ? Double.valueOf(body.get("amount").toString()) : 0.0;
            String startDateStr = body.get("startDate").toString();
            String endDateStr   = body.get("endDate").toString();

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));

            // Check if contract already exists for this registration
            if (registrationId != null) {
                boolean exists = contractRepository.findByStudentId(studentId).stream()
                    .anyMatch(c -> registrationId.equals(c.getRegistrationId()) && 
                                  ("ACTIVE".equals(c.getStatus()) || "PENDING_STUDENT".equals(c.getStatus())));
                if (exists) {
                    throw new RuntimeException("Đã tồn tại hợp đồng cho đơn đăng ký này");
                }
            }

            Contract contract = Contract.builder()
                    .student(student)
                    .room(room)
                    .startDate(LocalDate.parse(startDateStr))
                    .endDate(LocalDate.parse(endDateStr))
                    .amount(amount)
                    .registrationId(registrationId)
                    .status("PENDING_STUDENT")
                    .build();
            
            contract = contractRepository.save(contract);

            if (registrationId != null) {
                registrationRepository.findById(registrationId).ifPresent(reg -> {
                    reg.setStatus(RegistrationStatus.CONTRACT_CREATED);
                    registrationRepository.save(reg);
                });
            }

            // Create Notification for the student (targetUserId = student's ID as stored in localStorage)
            Notification notif = Notification.builder()
                    .title("Hợp đồng phòng mới (" + room.getRoomCode() + ")")
                    .content("Ban quản lý đã gửi hợp đồng phòng " + room.getRoomCode() + " cho bạn xem và xác nhận.")
                    .createDate(LocalDateTime.now())
                    .targetRole("STUDENT")
                    .targetUserId(student.getId())
                    .build();
            notificationRepository.save(notif);

            return ResponseEntity.status(HttpStatus.CREATED).body(contract);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/terminate")
    public ResponseEntity<?> terminateContract(@PathVariable Long id) {
        return contractRepository.findById(id).map(c -> {
            c.setStatus("TERMINATED");
            c.setEndDate(LocalDate.now());
            contractRepository.save(c);

            // Clear the student's room assignment when contract is terminated
            Student student = c.getStudent();
            if (student != null) {
                Room oldRoom = student.getRoom();
                student.setRoom(null);
                studentRepository.save(student);

                // Decrease room capacity
                if (oldRoom != null) {
                    if (oldRoom.getCurrentCapacity() > 0) {
                        oldRoom.setCurrentCapacity(oldRoom.getCurrentCapacity() - 1);
                        roomRepository.save(oldRoom);
                    }
                }

                // Notify student about contract termination
                String roomCode = c.getRoom() != null ? c.getRoom().getRoomCode() : "";
                notificationRepository.save(Notification.builder()
                        .title("Hợp đồng phòng " + roomCode + " đã bị chấm dứt ⚠️")
                        .content("Ban quản lý đã chấm dứt hợp đồng phòng " + roomCode
                                + " của bạn kể từ ngày " + LocalDate.now()
                                + ". Vui lòng liên hệ ban quản lý ký túc xá để biết thêm thông tin.")
                        .createDate(LocalDateTime.now())
                        .targetRole("STUDENT")
                        .targetUserId(student.getId())
                        .isRead(false)
                        .build());
            }

            return ResponseEntity.ok(contractRepository.findById(id).orElse(c));
        }).orElse(ResponseEntity.notFound().build());
    }


    /**
     * PUT /api/contracts/{id}/renew
     * Body: { months: 6 }  → extends the contract endDate by N months
     */
    @PutMapping("/{id}/renew")
    public ResponseEntity<?> renewContract(@PathVariable Long id,
                                           @RequestBody java.util.Map<String, Object> body) {
        return contractRepository.findById(id).map(c -> {
            if (!"ACTIVE".equals(c.getStatus()))
                return ResponseEntity.badRequest().body("Chỉ có thể gia hạn hợp đồng đang hiệu lực");

            int months = Integer.parseInt(body.getOrDefault("months", 6).toString());
            LocalDate newEnd = (c.getEndDate() != null ? c.getEndDate() : LocalDate.now()).plusMonths(months);
            c.setEndDate(newEnd);
            contractRepository.save(c);

            // Notify student
            Student student = c.getStudent();
            if (student != null) {
                String roomCode = c.getRoom() != null ? c.getRoom().getRoomCode() : "";
                notificationRepository.save(Notification.builder()
                        .title("Hợp đồng phòng " + roomCode + " được gia hạn")
                        .content("Hợp đồng phòng " + roomCode + " của bạn đã được gia hạn thêm "
                                + months + " tháng. Ngày kết thúc mới: " + newEnd + ".")
                        .createDate(LocalDateTime.now())
                        .targetRole("STUDENT")
                        .targetUserId(student.getId())
                        .isRead(false)
                        .build());
            }
            return ResponseEntity.ok(c);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptContract(@PathVariable Long id) {
        return contractRepository.findById(id).map(c -> {
            if (!"PENDING_STUDENT".equals(c.getStatus())) {
                throw new RuntimeException("Hợp đồng không ở trạng thái chờ xác nhận");
            }
            c.setStatus("ACTIVE");
            contractRepository.save(c);

            // Assign room to student
            Student student = c.getStudent();
            Room room = c.getRoom();
            student.setRoom(room);
            studentRepository.save(student);

            // Increase room capacity
            if (room.getCurrentCapacity() < room.getMaxCapacity()) {
                room.setCurrentCapacity(room.getCurrentCapacity() + 1);
                roomRepository.save(room);
            }

            // Approve registration
            if (c.getRegistrationId() != null) {
                registrationRepository.findById(c.getRegistrationId()).ifPresent(reg -> {
                    reg.setStatus(RegistrationStatus.APPROVED);
                    registrationRepository.save(reg);
                });
            }

            // Create Notification for Admins
            Notification notif = Notification.builder()
                    .title("Hợp đồng phòng " + room.getRoomCode() + " đã được xác nhận")
                    .content("Sinh viên " + student.getFullName() + " (" + student.getStudentCode() + ") đã đồng ý Hợp đồng phòng " + room.getRoomCode() + ".")
                    .createDate(LocalDateTime.now())
                    .targetRole("ADMIN")
                    .build();
            notificationRepository.save(notif);

            return ResponseEntity.ok(c);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectContract(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return contractRepository.findById(id).map(c -> {
            if (!"PENDING_STUDENT".equals(c.getStatus())) {
                throw new RuntimeException("Hợp đồng không ở trạng thái chờ xác nhận");
            }
            c.setStatus("REJECTED_BY_STUDENT");
            c.setRejectReason(body.get("reason"));
            contractRepository.save(c);

            // Reject registration
            if (c.getRegistrationId() != null) {
                registrationRepository.findById(c.getRegistrationId()).ifPresent(reg -> {
                    reg.setStatus(RegistrationStatus.REJECTED);
                    registrationRepository.save(reg);
                });
            }

            Student student = c.getStudent();
            Room room = c.getRoom();
            
            // Create Notification for Admins
            Notification notif = Notification.builder()
                    .title("Hợp đồng phòng " + room.getRoomCode() + " bị từ chối")
                    .content("Sinh viên " + student.getFullName() + " (" + student.getStudentCode() + ") đã từ chối Hợp đồng phòng " + room.getRoomCode() + ". Lý do: " + body.get("reason"))
                    .createDate(LocalDateTime.now())
                    .targetRole("ADMIN")
                    .build();
            notificationRepository.save(notif);

            return ResponseEntity.ok(c);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContract(@PathVariable Long id) {
        if (!contractRepository.existsById(id)) return ResponseEntity.notFound().build();
        contractRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        return contractRepository.findById(id).map(contract -> {
            byte[] pdfContents = contractPdfService.generateContractPdf(contract);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hop_dong_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfContents);
        }).orElse(ResponseEntity.notFound().build());
    }
}
