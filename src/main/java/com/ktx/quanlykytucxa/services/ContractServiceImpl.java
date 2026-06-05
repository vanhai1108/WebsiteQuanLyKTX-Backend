package com.ktx.quanlykytucxa.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ktx.quanlykytucxa.entities.Contract;
import com.ktx.quanlykytucxa.entities.Notification;
import com.ktx.quanlykytucxa.entities.RegistrationStatus;
import com.ktx.quanlykytucxa.entities.Room;
import com.ktx.quanlykytucxa.entities.Student;
import com.ktx.quanlykytucxa.repositories.ContractRepository;
import com.ktx.quanlykytucxa.repositories.NotificationRepository;
import com.ktx.quanlykytucxa.repositories.RegistrationRepository;
import com.ktx.quanlykytucxa.repositories.RoomRepository;
import com.ktx.quanlykytucxa.repositories.StudentRepository;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired private ContractRepository contractRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private ContractPdfService contractPdfService;

    @Override
    public ResponseEntity<List<Contract>> getAll() { return ResponseEntity.ok(contractRepository.findAll()); }

    @Override
    public ResponseEntity<List<Contract>> getByStudent(Long studentId) { return ResponseEntity.ok(contractRepository.findByStudentId(studentId)); }

    @Override
    @Transactional
    public ResponseEntity<?> createContract(Map<String, Object> body) {
        try {
            Long studentId = Long.valueOf(body.get("studentId").toString());
            Long roomId = Long.valueOf(body.get("roomId").toString());
            Long registrationId = body.get("registrationId") != null ? Long.valueOf(body.get("registrationId").toString()) : null;
            Double amount = body.get("amount") != null ? Double.valueOf(body.get("amount").toString()) : 0.0;
            String startDateStr = body.get("startDate").toString();
            String endDateStr = body.get("endDate").toString();

            Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));

            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            if (endDate.isBefore(startDate)) {
                throw new RuntimeException("Ngày kết thúc không thể nhỏ hơn ngày bắt đầu");
            }

            if (registrationId != null) {
                boolean exists = contractRepository.findByStudentId(studentId).stream()
                        .anyMatch(c -> registrationId.equals(c.getRegistrationId()) && ("ACTIVE".equals(c.getStatus()) || "PENDING_STUDENT".equals(c.getStatus())));
                if (exists) throw new RuntimeException("Đã tồn tại hợp đồng cho đơn đăng ký này");
            }

            Contract contract = Contract.builder()
                    .student(student)
                    .room(room)
                    .startDate(startDate)
                    .endDate(endDate)
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

    @Override
    public ResponseEntity<?> terminateContract(Long id) {
        return contractRepository.findById(id).map(c -> {
            c.setStatus("TERMINATED");
            c.setEndDate(LocalDate.now());
            contractRepository.save(c);
            Student student = c.getStudent();
            if (student != null) {
                Room oldRoom = student.getRoom();
                student.setRoom(null);
                studentRepository.save(student);
                if (oldRoom != null && oldRoom.getCurrentCapacity() > 0) {
                    oldRoom.setCurrentCapacity(oldRoom.getCurrentCapacity() - 1);
                    roomRepository.save(oldRoom);
                }
                String roomCode = c.getRoom() != null ? c.getRoom().getRoomCode() : "";
                notificationRepository.save(Notification.builder().title("Hợp đồng phòng " + roomCode + " đã bị chấm dứt ⚠️").content("Ban quản lý đã chấm dứt hợp đồng phòng " + roomCode + " của bạn kể từ ngày " + LocalDate.now() + ". Vui lòng liên hệ ban quản lý ký túc xá để biết thêm thông tin.").createDate(LocalDateTime.now()).targetRole("STUDENT").targetUserId(student.getId()).isRead(false).build());
            }
            return ResponseEntity.ok(contractRepository.findById(id).orElse(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> renewContract(Long id, Map<String, Object> body) {
        return contractRepository.findById(id).map(c -> {
            if (!"ACTIVE".equals(c.getStatus())) return ResponseEntity.badRequest().body("Chỉ có thể gia hạn hợp đồng đang hiệu lực");
            int months = Integer.parseInt(body.getOrDefault("months", 6).toString());
            LocalDate newEnd = (c.getEndDate() != null ? c.getEndDate() : LocalDate.now()).plusMonths(months);//lấy ngày kt cộng thêm số tháng gia hạn
            c.setEndDate(newEnd);
            contractRepository.save(c);
            Student student = c.getStudent();
            if (student != null) {
                String roomCode = c.getRoom() != null ? c.getRoom().getRoomCode() : "";
                notificationRepository.save(Notification.builder().title("Hợp đồng phòng " + roomCode + " được gia hạn").content("Hợp đồng phòng " + roomCode + " của bạn đã được gia hạn thêm " + months + " tháng. Ngày kết thúc mới: " + newEnd + ".").createDate(LocalDateTime.now()).targetRole("STUDENT").targetUserId(student.getId()).isRead(false).build());
            }
            return ResponseEntity.ok(c);
        }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> acceptContract(Long id) {
        return contractRepository.findById(id).map(c -> {
            if (!"PENDING_STUDENT".equals(c.getStatus())) throw new RuntimeException("Hợp đồng không ở trạng thái chờ xác nhận");
            c.setStatus("ACTIVE");
            contractRepository.save(c);
            Student student = c.getStudent();
            Room room = c.getRoom();
            student.setRoom(room);
            studentRepository.save(student);
            if (room.getCurrentCapacity() < room.getMaxCapacity()) {
                room.setCurrentCapacity(room.getCurrentCapacity() + 1);
                roomRepository.save(room);
            }
            if (c.getRegistrationId() != null) {
                registrationRepository.findById(c.getRegistrationId()).ifPresent(reg -> {
                    reg.setStatus(RegistrationStatus.APPROVED);
                    registrationRepository.save(reg);
                });
            }
            notificationRepository.save(Notification.builder().title("Hợp đồng phòng " + room.getRoomCode() + " đã được xác nhận").content("Sinh viên " + student.getFullName() + " (" + student.getStudentCode() + ") đã đồng ý Hợp đồng phòng " + room.getRoomCode() + ".").createDate(LocalDateTime.now()).targetRole("ADMIN").build());
            return ResponseEntity.ok(c);
        }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> rejectContract(Long id, Map<String, String> body) {
        return contractRepository.findById(id).map(c -> {
            if (!"PENDING_STUDENT".equals(c.getStatus())) throw new RuntimeException("Hợp đồng không ở trạng thái chờ xác nhận");
            c.setStatus("REJECTED_BY_STUDENT");
            c.setRejectReason(body.get("reason"));
            contractRepository.save(c);
            if (c.getRegistrationId() != null) {
                registrationRepository.findById(c.getRegistrationId()).ifPresent(reg -> {
                    reg.setStatus(RegistrationStatus.REJECTED);
                    registrationRepository.save(reg);
                });
            }
            Student student = c.getStudent();
            Room room = c.getRoom();
            notificationRepository.save(Notification.builder().title("Hợp đồng phòng " + room.getRoomCode() + " bị từ chối").content("Sinh viên " + student.getFullName() + " (" + student.getStudentCode() + ") đã từ chối Hợp đồng phòng " + room.getRoomCode() + ". Lý do: " + body.get("reason")).createDate(LocalDateTime.now()).targetRole("ADMIN").build());
            return ResponseEntity.ok(c);
        }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> deleteContract(Long id) {
        if (!contractRepository.existsById(id)) return ResponseEntity.notFound().build();
        contractRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<byte[]> downloadPdf(Long id) {
        return contractRepository.findById(id).map(contract -> {
            byte[] pdfContents = contractPdfService.generateContractPdf(contract);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hop_dong_" + id + ".pdf").contentType(MediaType.APPLICATION_PDF).body(pdfContents);
        }).orElse(ResponseEntity.notFound().build());
    }
}
