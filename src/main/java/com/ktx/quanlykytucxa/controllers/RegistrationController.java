package com.ktx.quanlykytucxa.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ktx.quanlykytucxa.entities.Registration;
import com.ktx.quanlykytucxa.services.RegistrationService;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin("*")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping
    public ResponseEntity<List<Registration>> getAllRegistrations() {
        return ResponseEntity.ok(registrationService.getAllRegistrations());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Registration>> getRegistrationsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByStudentId(studentId));
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitRegistration(@RequestParam Long studentId, @RequestParam Long roomId) {
        try {
            Registration newReg = registrationService.submitRegistration(studentId, roomId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newReg);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveRegistration(@PathVariable Long id) {
        try {
            Registration approvedReg = registrationService.approveRegistration(id);
            return ResponseEntity.ok(approvedReg);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectRegistration(@PathVariable Long id) {
        try {
            Registration rejectedReg = registrationService.rejectRegistration(id);
            return ResponseEntity.ok(rejectedReg);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegistration(@PathVariable Long id) {
        try {
            registrationService.deleteRegistration(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
