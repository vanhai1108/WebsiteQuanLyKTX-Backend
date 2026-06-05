package com.ktx.quanlykytucxa.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktx.quanlykytucxa.entities.Contract;
import com.ktx.quanlykytucxa.services.ContractService;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin("*")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @GetMapping
    public ResponseEntity<List<Contract>> getAll() { return contractService.getAll(); }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Contract>> getByStudent(@PathVariable Long studentId) { return contractService.getByStudent(studentId); }

    @PostMapping
    public ResponseEntity<?> createContract(@RequestBody Map<String, Object> body) { return contractService.createContract(body); }

    @PutMapping("/{id}/terminate")
    public ResponseEntity<?> terminateContract(@PathVariable Long id) { return contractService.terminateContract(id); }

    @PutMapping("/{id}/renew")
    public ResponseEntity<?> renewContract(@PathVariable Long id, @RequestBody Map<String, Object> body) { return contractService.renewContract(id, body); }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptContract(@PathVariable Long id) { return contractService.acceptContract(id); }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectContract(@PathVariable Long id, @RequestBody Map<String, String> body) { return contractService.rejectContract(id, body); }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContract(@PathVariable Long id) { return contractService.deleteContract(id); }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) { return contractService.downloadPdf(id); }
}
