package com.ktx.quanlykytucxa.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ktx.quanlykytucxa.entities.Notification;
import com.ktx.quanlykytucxa.entities.Room;
import com.ktx.quanlykytucxa.entities.RoomTransferRequest;
import com.ktx.quanlykytucxa.entities.Student;
import com.ktx.quanlykytucxa.repositories.NotificationRepository;
import com.ktx.quanlykytucxa.repositories.RoomRepository;
import com.ktx.quanlykytucxa.repositories.RoomTransferRequestRepository;
import com.ktx.quanlykytucxa.repositories.StudentRepository;

@Service
public class RoomTransferServiceImpl implements RoomTransferService {

    @Autowired private RoomTransferRequestRepository transferRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private NotificationRepository notificationRepository;

    @Override
    public ResponseEntity<List<RoomTransferRequest>> getAll() {
        return ResponseEntity.ok(transferRepository.findAllByOrderByRequestDateDesc());
    }

    @Override
    public ResponseEntity<List<RoomTransferRequest>> getByStudent(Long studentId) {
        return ResponseEntity.ok(transferRepository.findByStudentIdOrderByRequestDateDesc(studentId));
    }

    @Override
    public ResponseEntity<?> createRequest(Map<String, Object> body) {
        try {
            Long studentId = Long.valueOf(body.get("studentId").toString());
            Long requestedRoomId = Long.valueOf(body.get("requestedRoomId").toString());
            String reason = body.getOrDefault("reason", "").toString();

            Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));
            if (student.getRoom() == null) return ResponseEntity.badRequest().body("Bạn chưa được xếp phòng, không thể yêu cầu đổi phòng");

            Room currentRoom = student.getRoom();
            Room requestedRoom = roomRepository.findById(requestedRoomId).orElseThrow(() -> new RuntimeException("Không tìm thấy phòng yêu cầu"));
            if (currentRoom.getId().equals(requestedRoomId)) return ResponseEntity.badRequest().body("Phòng yêu cầu trùng với phòng hiện tại");
            if (transferRepository.existsByStudentIdAndStatus(studentId, "PENDING")) return ResponseEntity.badRequest().body("Bạn đã có yêu cầu đổi phòng đang chờ xử lý");
            if (requestedRoom.getCurrentCapacity() >= requestedRoom.getMaxCapacity()) return ResponseEntity.badRequest().body("Phòng yêu cầu đã đầy");

            RoomTransferRequest req = RoomTransferRequest.builder()
                    .student(student)
                    .currentRoom(currentRoom)
                    .requestedRoom(requestedRoom)
                    .reason(reason)
                    .status("PENDING")
                    .requestDate(LocalDateTime.now())
                    .build();
            transferRepository.save(req);

            notificationRepository.save(Notification.builder()
                    .title("Yêu cầu đổi phòng: " + student.getFullName())
                    .content("Sinh viên " + student.getFullName() + " (" + student.getStudentCode()
                            + ") xin đổi từ phòng " + currentRoom.getRoomCode()
                            + " sang phòng " + requestedRoom.getRoomCode()
                            + ". Lý do: " + reason)
                    .createDate(LocalDateTime.now())
                    .targetRole("ADMIN")
                    .isRead(false)
                    .build());

            return ResponseEntity.status(HttpStatus.CREATED).body(req);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> approve(Long id) {
        return transferRepository.findById(id).map(req -> {
            if (!"PENDING".equals(req.getStatus())) return ResponseEntity.badRequest().body("Yêu cầu không còn chờ xử lý");
            Student student = req.getStudent();
            Room newRoom = req.getRequestedRoom();
            if (newRoom.getCurrentCapacity() >= newRoom.getMaxCapacity()) return ResponseEntity.badRequest().body("Phòng yêu cầu đã đầy");
            Room oldRoom = req.getCurrentRoom();
            oldRoom.setCurrentCapacity(Math.max(0, oldRoom.getCurrentCapacity() - 1));
            newRoom.setCurrentCapacity(newRoom.getCurrentCapacity() + 1);
            roomRepository.save(oldRoom);
            roomRepository.save(newRoom);
            student.setRoom(newRoom);
            studentRepository.save(student);
            req.setStatus("APPROVED");
            transferRepository.save(req);
            notificationRepository.save(Notification.builder()
                    .title("Yêu cầu đổi phòng được duyệt")
                    .content("Yêu cầu đổi phòng của bạn sang phòng " + newRoom.getRoomCode() + " đã được chấp thuận. Vui lòng hoàn tất thủ tục chuyển phòng.")
                    .createDate(LocalDateTime.now())
                    .targetRole("STUDENT")
                    .targetUserId(student.getId())
                    .isRead(false)
                    .build());
            return ResponseEntity.ok(req);
        }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> reject(Long id, Map<String, Object> body) {
        return transferRepository.findById(id).map(req -> {
            if (!"PENDING".equals(req.getStatus())) return ResponseEntity.badRequest().body("Yêu cầu không còn chờ xử lý");
            String reason = body.getOrDefault("reason", "Không đủ điều kiện").toString();
            req.setStatus("REJECTED");
            req.setRejectReason(reason);
            transferRepository.save(req);
            notificationRepository.save(Notification.builder()
                    .title("Yêu cầu đổi phòng bị từ chối")
                    .content("Yêu cầu đổi phòng của bạn sang phòng " + req.getRequestedRoom().getRoomCode() + " đã bị từ chối. Lý do: " + reason)
                    .createDate(LocalDateTime.now())
                    .targetRole("STUDENT")
                    .targetUserId(req.getStudent().getId())
                    .isRead(false)
                    .build());
            return ResponseEntity.ok(req);
        }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> delete(Long id) {
        if (!transferRepository.existsById(id)) return ResponseEntity.notFound().build();
        transferRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
