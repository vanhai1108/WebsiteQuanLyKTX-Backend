package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ktx.quanlykytucxa.entities.Contract;

public interface ContractService {
    ResponseEntity<List<Contract>> getAll();
    ResponseEntity<List<Contract>> getByStudent(Long studentId);
    ResponseEntity<?> createContract(Map<String, Object> body);
    ResponseEntity<?> terminateContract(Long id);
    ResponseEntity<?> renewContract(Long id, Map<String, Object> body);
    ResponseEntity<?> acceptContract(Long id);
    ResponseEntity<?> rejectContract(Long id, Map<String, String> body);
    ResponseEntity<?> deleteContract(Long id);
    ResponseEntity<byte[]> downloadPdf(Long id);
}
