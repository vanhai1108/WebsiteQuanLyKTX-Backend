package com.ktx.quanlykytucxa.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ktx.quanlykytucxa.entities.Registration;
import com.ktx.quanlykytucxa.entities.RegistrationStatus;
import com.ktx.quanlykytucxa.entities.Room;
import com.ktx.quanlykytucxa.entities.Student;
import com.ktx.quanlykytucxa.repositories.RegistrationRepository;
import com.ktx.quanlykytucxa.repositories.RoomRepository;
import com.ktx.quanlykytucxa.repositories.StudentRepository;
import com.ktx.quanlykytucxa.repositories.NotificationRepository;
import com.ktx.quanlykytucxa.entities.Notification;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public List<Registration> getRegistrationsByStudentId(Long studentId) {
        return registrationRepository.findByStudentId(studentId);
    }

    @Override
    public Optional<Registration> getRegistrationById(Long id) {
        return registrationRepository.findById(id);
    }

    @Override
    public Registration submitRegistration(Long studentId, Long roomId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        if (student.getRoom() != null) {
            throw new RuntimeException("Sinh viên đã có phòng, không thể đăng ký thêm");
        }

        boolean hasActiveRegistration = registrationRepository.findByStudentId(studentId).stream()
                .anyMatch(r -> r.getStatus() == RegistrationStatus.PENDING || r.getStatus() == RegistrationStatus.CONTRACT_CREATED);

        if (hasActiveRegistration) {
            throw new RuntimeException("Bạn đang có đơn đăng ký chờ xử lý hoặc hợp đồng chờ ký, không thể đăng ký thêm phòng khác.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));

        if (room.getCurrentCapacity() >= room.getMaxCapacity()) {
            throw new RuntimeException("Phòng đã đầy");
        }

        if (room.getLocked() != null && room.getLocked()) {
            throw new RuntimeException("Phòng đang tạm khóa: " + room.getLockReason());
        }

        Registration registration = Registration.builder()
                .student(student)
                .room(room)
                .requestDate(LocalDateTime.now())
                .status(RegistrationStatus.PENDING)
                .build();

        Registration savedReg = registrationRepository.save(registration);

        Notification notif = Notification.builder()
                .title("Đơn đăng ký phòng mới (" + room.getRoomCode() + ")")
                .content("Sinh viên " + student.getFullName() + " (" + student.getStudentCode() + ") vừa gửi yêu cầu đăng ký phòng " + room.getRoomCode() + ".")
                .createDate(LocalDateTime.now())
                .targetRole("ADMIN")
                .build();
        notificationRepository.save(notif);

        return savedReg;
    }

    @Override
    public Registration approveRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký"));
        
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new RuntimeException("Đơn không ở trạng thái chờ duyệt");
        }

        Room room = registration.getRoom();
        if (room.getCurrentCapacity() >= room.getMaxCapacity()) {
            registration.setStatus(RegistrationStatus.REJECTED);
            registrationRepository.save(registration);
            throw new RuntimeException("Phòng đã đầy, đơn đăng ký bị từ chối tự động");
        }

        Student student = registration.getStudent();
        
        // Cập nhật thông tin phòng cho sinh viên và số lượng phòng
        student.setRoom(room);
        room.setCurrentCapacity(room.getCurrentCapacity() + 1);
        roomRepository.save(room);
        studentRepository.save(student);

        registration.setStatus(RegistrationStatus.APPROVED);
        Registration saved = registrationRepository.save(registration);

        // Notify student
        Notification notifStudent = Notification.builder()
                .title("Đơn đăng ký phòng được chấp thuận ✅")
                .content("Đơn đăng ký phòng " + room.getRoomCode() + " của bạn đã được Admin duyệt thành công. Chúc mừng bạn!")
                .createDate(LocalDateTime.now())
                .targetRole("STUDENT")
                .targetUserId(student.getId())
                .build();
        notificationRepository.save(notifStudent);

        return saved;
    }

    @Override
    public Registration rejectRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký"));
        
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new RuntimeException("Đơn không ở trạng thái chờ duyệt");
        }

        registration.setStatus(RegistrationStatus.REJECTED);
        Registration saved = registrationRepository.save(registration);

        // Notify student
        Student student = registration.getStudent();
        Room room = registration.getRoom();
        Notification notifStudent = Notification.builder()
                .title("Đơn đăng ký phòng bị từ chối ❌")
                .content("Rất tiếc, đơn đăng ký phòng " + room.getRoomCode() + " của bạn đã bị Admin từ chối. Vui lòng liên hệ ban quản lý để biết thêm thông tin.")
                .createDate(LocalDateTime.now())
                .targetRole("STUDENT")
                .targetUserId(student.getId())
                .build();
        notificationRepository.save(notifStudent);

        return saved;
    }

    @Override
    public void deleteRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đăng ký"));
        registrationRepository.delete(registration);
    }
}
