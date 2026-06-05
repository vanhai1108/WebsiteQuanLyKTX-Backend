package com.ktx.quanlykytucxa.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ktx.quanlykytucxa.entities.Notification;

public interface NotificationService {
    ResponseEntity<List<Notification>> getAll(String role, Long userId);
    ResponseEntity<?> markAllRead(String role, Long userId);
    ResponseEntity<Long> getUnreadCount(String role, Long userId);
    ResponseEntity<?> createNotification(Notification notification);
    ResponseEntity<?> deleteNotification(Long id);
}
