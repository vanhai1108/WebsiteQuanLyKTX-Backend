package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ktx.quanlykytucxa.entities.Student;
import com.ktx.quanlykytucxa.repositories.ContractRepository;
import com.ktx.quanlykytucxa.repositories.InvoiceRepository;
import com.ktx.quanlykytucxa.repositories.NotificationRepository;
import com.ktx.quanlykytucxa.repositories.RegistrationRepository;
import com.ktx.quanlykytucxa.repositories.RoomRepository;
import com.ktx.quanlykytucxa.repositories.RoomTransferRequestRepository;
import com.ktx.quanlykytucxa.repositories.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private RoomTransferRequestRepository roomTransferRequestRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    public Student createStudent(Student student) {
        if (studentRepository.existsByStudentCode(student.getStudentCode())) {
            throw new RuntimeException("Mã sinh viên đã tồn tại!");
        }
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Long id, Student studentDetails) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        student.setStudentCode(studentDetails.getStudentCode());
        student.setFullName(studentDetails.getFullName());
        student.setClassName(studentDetails.getClassName());
        student.setPhone(studentDetails.getPhone());
        student.setEmail(studentDetails.getEmail());
        
        return studentRepository.save(student);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        // Giảm sức chứa phòng nếu sinh viên đang ở phòng
        if (student.getRoom() != null) {
            var room = student.getRoom();
            if (room.getCurrentCapacity() > 0) {
                room.setCurrentCapacity(room.getCurrentCapacity() - 1);
                roomRepository.save(room);
            }
        }

        // Xóa các bản ghi liên quan trước khi xóa sinh viên
        registrationRepository.deleteAll(registrationRepository.findByStudentId(id));
        contractRepository.deleteAll(contractRepository.findByStudentId(id));
        invoiceRepository.deleteAll(invoiceRepository.findByStudentId(id));
        roomTransferRequestRepository.deleteAll(roomTransferRequestRepository.findByStudentIdOrderByRequestDateDesc(id));

        // Xóa thông báo gửi đến sinh viên này
        notificationRepository.deleteAll(notificationRepository.findByTargetUserId(id));

        studentRepository.delete(student);
    }
}
