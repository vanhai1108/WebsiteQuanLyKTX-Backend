package com.ktx.quanlykytucxa.services;

import com.ktx.quanlykytucxa.dto.AdminDashboardResponse;
import com.ktx.quanlykytucxa.dto.StudentDashboardResponse;

public interface DashboardService {
    AdminDashboardResponse getAdminDashboardMetrics();
    StudentDashboardResponse getStudentDashboardMetrics(Long userId);
}
