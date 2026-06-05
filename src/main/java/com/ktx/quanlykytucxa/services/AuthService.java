package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ktx.quanlykytucxa.dto.LoginRequest;
import com.ktx.quanlykytucxa.entities.Student;

public interface AuthService {
    ResponseEntity<?> login(LoginRequest request);
    ResponseEntity<?> createAccount(Map<String, String> body);
    ResponseEntity<List<Student>> getStudentAccounts();
    ResponseEntity<?> deleteUserAccount(Long userId);
    ResponseEntity<?> changePassword(Long userId, Map<String, String> body);
}
