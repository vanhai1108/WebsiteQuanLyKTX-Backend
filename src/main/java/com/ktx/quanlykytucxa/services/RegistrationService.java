package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Optional;

import com.ktx.quanlykytucxa.entities.Registration;

public interface RegistrationService {
    List<Registration> getAllRegistrations();
    List<Registration> getRegistrationsByStudentId(Long studentId);
    Optional<Registration> getRegistrationById(Long id);
    Registration submitRegistration(Long studentId, Long roomId);
    Registration approveRegistration(Long id);
    Registration rejectRegistration(Long id);
    void deleteRegistration(Long id);
}
