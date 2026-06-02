package com.ktx.quanlykytucxa.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardResponse {
    private long totalStudents;
    private long totalRooms;
    private long availableRooms;
    private double monthlyRevenue;
    private long pendingRegistrations;
    private java.util.List<PendingRegistrationInfo> recentPending;

    @Data
    @Builder
    public static class PendingRegistrationInfo {
        private Long id;
        private String studentName;
        private String roomCode;
        private java.time.LocalDateTime requestDate;
    }
}
