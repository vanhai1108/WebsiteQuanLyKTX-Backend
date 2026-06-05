package com.ktx.quanlykytucxa.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.ktx.quanlykytucxa.dto.LoginRequest;
import com.ktx.quanlykytucxa.entities.Student;
import com.ktx.quanlykytucxa.services.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody Map<String, String> body) {
        return authService.createAccount(body);
    }

    @GetMapping("/student-accounts")
    public ResponseEntity<List<Student>> getStudentAccounts() {
        return authService.getStudentAccounts();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUserAccount(@PathVariable Long userId) {
        return authService.deleteUserAccount(userId);
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<?> changePassword(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        return authService.changePassword(userId, body);
    }
}
