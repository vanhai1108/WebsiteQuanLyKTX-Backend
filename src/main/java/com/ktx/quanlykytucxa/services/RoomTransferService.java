package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ktx.quanlykytucxa.entities.RoomTransferRequest;

public interface RoomTransferService {
    ResponseEntity<List<RoomTransferRequest>> getAll();
    ResponseEntity<List<RoomTransferRequest>> getByStudent(Long studentId);
    ResponseEntity<?> createRequest(Map<String, Object> body);
    ResponseEntity<?> approve(Long id);
    ResponseEntity<?> reject(Long id, Map<String, Object> body);
    ResponseEntity<?> delete(Long id);
}
