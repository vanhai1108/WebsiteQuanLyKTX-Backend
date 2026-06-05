package com.ktx.quanlykytucxa.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ktx.quanlykytucxa.entities.Notification;
import com.ktx.quanlykytucxa.services.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAll(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId) {
        return notificationService.getAll(role, userId);
    }

    @PutMapping("/mark-read")
    public ResponseEntity<?> markAllRead(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId) {
        return notificationService.markAllRead(role, userId);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId) {
        return notificationService.getUnreadCount(role, userId);
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        return notificationService.deleteNotification(id);
    }
}
