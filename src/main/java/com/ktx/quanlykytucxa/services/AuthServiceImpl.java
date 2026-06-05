package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ktx.quanlykytucxa.dto.LoginRequest;
import com.ktx.quanlykytucxa.entities.Role;
import com.ktx.quanlykytucxa.entities.Student;
import com.ktx.quanlykytucxa.entities.User;
import com.ktx.quanlykytucxa.repositories.StudentRepository;
import com.ktx.quanlykytucxa.repositories.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> login(LoginRequest request) {
        try {
            System.out.println("Login attempt: " + request.getUsername());
            Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                if (!user.getUsername().equals(request.getUsername())) {
                    return ResponseEntity.badRequest().body("Tài khoản hoặc mật khẩu không chính xác");
                }
                if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    if (user.getRole() == Role.STUDENT) {
                        Optional<Student> studentOpt = studentRepository.findByUser_Id(user.getId());
                        if (studentOpt.isPresent()) {
                            Student student = studentOpt.get();
                            return ResponseEntity.ok(Map.of(
                                    "id", student.getId(),
                                    "username", user.getUsername(),
                                    "role", user.getRole().name(),
                                    "fullName", student.getFullName(),
                                    "studentCode", student.getStudentCode()));
                        }
                    }
                    return ResponseEntity.ok(Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "role", user.getRole().name()));
                }
            } else {
                System.out.println("User not found!");
            }

            return ResponseEntity.badRequest().body("Tài khoản hoặc mật khẩu không chính xác");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi đăng nhập: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> createAccount(Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String studentIdStr = body.get("studentId");

        try {
            if (username == null || password == null || studentIdStr == null) {
                return ResponseEntity.badRequest().body("Vui lòng điền đầy đủ thông tin: tên đăng nhập, mật khẩu và ID sinh viên");
            }

            Long studentId = Long.valueOf(studentIdStr);
            Optional<Student> studentOpt = studentRepository.findById(studentId);

            if (studentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy sinh viên");
            }

            Student student = studentOpt.get();

            if (student.getUser() != null) {
                return ResponseEntity.badRequest().body("Sinh viên này đã có tài khoản");
            }

            if (userRepository.existsByUsername(username)) {
                return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại");
            }

            User newUser = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(Role.STUDENT)
                    .build();
            newUser = userRepository.save(newUser);

            student.setUser(newUser);
            studentRepository.save(student);

            return ResponseEntity.ok("Cấp tài khoản thành công!");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID sinh viên không hợp lệ");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi cấp tài khoản: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<List<Student>> getStudentAccounts() {
        return ResponseEntity.ok(studentRepository.findAll());
    }

    @Override
    public ResponseEntity<?> deleteUserAccount(Long userId) {
        try {
            if (!userRepository.existsById(userId)) {
                return ResponseEntity.notFound().build();
            }
            Optional<Student> studentOpt = studentRepository.findByUser_Id(userId);
            //tìm sinh viên đang liên kết với user này
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                student.setUser(null); // xóa mối liên kết giữa sinh viên và tài khoản
                studentRepository.save(student);
            }
            userRepository.deleteById(userId);
            return ResponseEntity.ok("Đã xóa tài khoản");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> changePassword(Long userId, Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        try {
            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Vui lòng cung cấp đầy đủ mật khẩu cũ và mật khẩu mới");
            }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok("Đổi mật khẩu thành công");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi đổi mật khẩu: " + e.getMessage());
        }
    }
}
