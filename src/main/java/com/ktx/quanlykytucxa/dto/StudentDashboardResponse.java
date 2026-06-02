package com.ktx.quanlykytucxa.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDashboardResponse {
    private String studentName;
    private String roomCode;
    private String building;
    private double currentDebt;
    private long activeContracts;
    private String registrationStatus;
}
