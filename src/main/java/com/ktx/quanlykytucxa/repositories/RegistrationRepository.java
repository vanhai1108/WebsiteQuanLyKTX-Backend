package com.ktx.quanlykytucxa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ktx.quanlykytucxa.entities.Registration;
import com.ktx.quanlykytucxa.entities.RegistrationStatus;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudentId(Long studentId);
    List<Registration> findByStudentIdOrderByRequestDateDesc(Long studentId);
    List<Registration> findByStatus(RegistrationStatus status);
    List<Registration> findByStatusOrderByRequestDateDesc(RegistrationStatus status);
    long countByStatus(RegistrationStatus status);
}
