package com.ktx.quanlykytucxa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ktx.quanlykytucxa.entities.RoomTransferRequest;

public interface RoomTransferRequestRepository extends JpaRepository<RoomTransferRequest, Long> {
    List<RoomTransferRequest> findByStudentIdOrderByRequestDateDesc(Long studentId);
    List<RoomTransferRequest> findAllByOrderByRequestDateDesc();
    boolean existsByStudentIdAndStatus(Long studentId, String status);
}
