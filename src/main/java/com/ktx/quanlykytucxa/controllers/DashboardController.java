package com.ktx.quanlykytucxa.controllers;

import com.ktx.quanlykytucxa.dto.AdminDashboardResponse;
import com.ktx.quanlykytucxa.dto.StudentDashboardResponse;
import com.ktx.quanlykytucxa.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin("*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        return ResponseEntity.ok(dashboardService.getAdminDashboardMetrics());
    }

    @GetMapping("/student/{userId}")
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getStudentDashboardMetrics(userId));
    }
}
