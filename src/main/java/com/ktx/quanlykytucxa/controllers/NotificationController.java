package com.ktx.quanlykytucxa.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.ktx.quanlykytucxa.entities.NotificationRead;
import com.ktx.quanlykytucxa.repositories.NotificationReadRepository;
import com.ktx.quanlykytucxa.repositories.NotificationRepository;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationReadRepository notificationReadRepository;

    private long readerUserId(String role, Long userId) {
        if ("ADMIN".equals(role)) return 0L;
        if ("STUDENT".equals(role) && userId != null) return userId;
        return -1L;
    }

    /**
     * GET /api/notifications?role=ADMIN        -> all admin broadcast notifications
     * GET /api/notifications?role=STUDENT&userId=5  -> notifications for student with id=5
     * GET /api/notifications                   -> all notifications (admin management view)
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAll(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId) {

        if (role == null && userId == null) {
            return ResponseEntity.ok(notificationRepository.findAllByOrderByCreateDateDesc());
        }

        List<Notification> all = notificationRepository.findAllByOrderByCreateDateDesc();
        List<Notification> filtered = all.stream().filter(n -> {
            // Global broadcast: visible to everyone
            if ("ALL".equals(n.getTargetRole())) return true;

            // If targeted to a specific user, ONLY match by exact userId
            if (n.getTargetUserId() != null) {
                return userId != null && userId.equals(n.getTargetUserId());
            }

            // Role-level broadcast (e.g., all admins)
            return role != null && role.equals(n.getTargetRole());
        }).toList();

        // Attach per-reader isRead without mutating the notification globally
        long readerId = readerUserId(role, userId);
        if (readerId >= 0) {
            Set<Long> notifIds = filtered.stream().map(Notification::getId).collect(Collectors.toSet());
            List<NotificationRead> reads = notificationReadRepository
                    .findByReaderRoleAndReaderUserIdAndNotificationIdIn(role, readerId, notifIds);
            Set<Long> readIds = reads.stream().map(NotificationRead::getNotificationId).collect(Collectors.toSet());
            filtered.forEach(n -> n.setIsRead(readIds.contains(n.getId())));
        }

        return ResponseEntity.ok(filtered);
    }

    /**
     * PUT /api/notifications/mark-read?role=ADMIN
     * PUT /api/notifications/mark-read?userId=5
     * Marks all matching notifications as read (isRead = true)
     */
    @PutMapping("/mark-read")
    public ResponseEntity<?> markAllRead(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId) {

        if (role == null) return ResponseEntity.badRequest().body("Thiếu role");
        long readerId = readerUserId(role, userId);
        if ("STUDENT".equals(role) && readerId < 0) return ResponseEntity.badRequest().body("Thiếu userId");

        List<Notification> all = notificationRepository.findAllByOrderByCreateDateDesc();
        List<Notification> matching = all.stream().filter(n -> {
            if ("ALL".equals(n.getTargetRole())) return true;
            if (n.getTargetUserId() != null) {
                return userId != null && userId.equals(n.getTargetUserId());
            }
            return role.equals(n.getTargetRole());
        }).toList();

        Set<Long> notifIds = matching.stream().map(Notification::getId).collect(Collectors.toSet());
        List<NotificationRead> existingReads = notificationReadRepository
                .findByReaderRoleAndReaderUserIdAndNotificationIdIn(role, readerId, notifIds);
        Set<Long> alreadyRead = existingReads.stream().map(NotificationRead::getNotificationId).collect(Collectors.toSet());

        List<NotificationRead> toCreate = matching.stream()
                .filter(n -> !alreadyRead.contains(n.getId()))
                .map(n -> NotificationRead.builder()
                        .notificationId(n.getId())
                        .readerRole(role)
                        .readerUserId(readerId)
                        .readAt(LocalDateTime.now())
                        .build())
                .toList();

        if (!toCreate.isEmpty()) {
            notificationReadRepository.saveAll(toCreate);
        }

        return ResponseEntity.ok("Đã đánh dấu " + toCreate.size() + " thông báo là đã đọc");
    }

    /**
     * GET /api/notifications/unread-count?role=ADMIN
     * GET /api/notifications/unread-count?role=STUDENT&userId=5
     * Returns the count of unread notifications for efficient polling
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId) {

        if (role == null) return ResponseEntity.badRequest().build();
        long readerId = readerUserId(role, userId);
        if ("STUDENT".equals(role) && readerId < 0) return ResponseEntity.badRequest().build();

        List<Notification> all = notificationRepository.findAllByOrderByCreateDateDesc();
        List<Notification> matching = all.stream().filter(n -> {
            if ("ALL".equals(n.getTargetRole())) return true;
            if (n.getTargetUserId() != null) {
                return userId != null && userId.equals(n.getTargetUserId());
            }
            return role.equals(n.getTargetRole());
        }).toList();

        Set<Long> notifIds = matching.stream().map(Notification::getId).collect(Collectors.toSet());
        List<NotificationRead> reads = notificationReadRepository
                .findByReaderRoleAndReaderUserIdAndNotificationIdIn(role, readerId, notifIds);
        Set<Long> readIds = reads.stream().map(NotificationRead::getNotificationId).collect(Collectors.toSet());

        long unread = matching.stream().filter(n -> !readIds.contains(n.getId())).count();
        return ResponseEntity.ok(unread);
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        try {
            notification.setCreateDate(LocalDateTime.now());
            if (notification.getIsRead() == null) notification.setIsRead(false);
            return ResponseEntity.status(HttpStatus.CREATED).body(notificationRepository.save(notification));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo thông báo: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        if (!notificationRepository.existsById(id)) return ResponseEntity.notFound().build();
        notificationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
