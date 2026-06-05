package com.ktx.quanlykytucxa.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ktx.quanlykytucxa.entities.RoomTransferRequest;
import com.ktx.quanlykytucxa.services.RoomTransferService;

@RestController
@RequestMapping("/api/room-transfers")
@CrossOrigin("*")
public class RoomTransferController {

    @Autowired private RoomTransferService transferService;

    @GetMapping
    public ResponseEntity<List<RoomTransferRequest>> getAll() { return transferService.getAll(); }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<RoomTransferRequest>> getByStudent(@PathVariable Long studentId) { return transferService.getByStudent(studentId); }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Object> body) { return transferService.createRequest(body); }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) { return transferService.approve(id); }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestBody Map<String, Object> body) { return transferService.reject(id, body); }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) { return transferService.delete(id); }
}
