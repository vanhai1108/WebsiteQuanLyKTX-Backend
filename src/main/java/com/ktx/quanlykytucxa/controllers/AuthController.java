package com.ktx.quanlykytucxa.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktx.quanlykytucxa.dto.LoginRequest;
import com.ktx.quanlykytucxa.entities.Role;
import com.ktx.quanlykytucxa.entities.Student;
import com.ktx.quanlykytucxa.entities.User;
import com.ktx.quanlykytucxa.repositories.StudentRepository;
import com.ktx.quanlykytucxa.repositories.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("Login attempt: " + request.getUsername());
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Check exact case-sensitive match for username
            if (!user.getUsername().equals(request.getUsername())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác");
            }
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // Build response with student id if STUDENT role
                if (user.getRole() == Role.STUDENT) {
                    Optional<Student> studentOpt = studentRepository.findByUser_Id(user.getId());
                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        // Return user object with embedded student id
                        Map<String, Object> response = Map.of(
                            "id", student.getId(),
                            "username", user.getUsername(),
                            "role", user.getRole().name(),
                            "fullName", student.getFullName(),
                            "studentCode", student.getStudentCode()
                        );
                        return ResponseEntity.ok(response);
                    }
                }
                // For non-student (e.g. ADMIN), avoid returning password field
                Map<String, Object> response = Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole().name()
                );
                return ResponseEntity.ok(response);
            }
        } else {
            System.out.println("User not found!");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác");
    }

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");
            String studentIdStr = body.get("studentId");

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

            // Create user
            User newUser = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(Role.STUDENT)
                    .build();
            newUser = userRepository.save(newUser);

            // Link student to user
            student.setUser(newUser);
            studentRepository.save(student);

            return ResponseEntity.status(HttpStatus.CREATED).body("Cấp tài khoản thành công!");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID sinh viên không hợp lệ");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cấp tài khoản: " + e.getMessage());
        }
    }

    @GetMapping("/student-accounts")
    public ResponseEntity<List<Student>> getStudentAccounts() {
        List<Student> students = studentRepository.findAll();
        return ResponseEntity.ok(students);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUserAccount(@PathVariable Long userId) {
        try {
            if (!userRepository.existsById(userId)) {
                return ResponseEntity.notFound().build();
            }
            // Find linked student and unlink
            Optional<Student> studentOpt = studentRepository.findByUser_Id(userId);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                student.setUser(null);
                studentRepository.save(student);
            }
            userRepository.deleteById(userId);
            return ResponseEntity.ok("Đã xóa tài khoản");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        try {
            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");

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
            return ResponseEntity.badRequest().body("Lỗi khi đổi mật khẩu: " + e.getMessage());
        }
    }
}
