package com.ktx.quanlykytucxa.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ktx.quanlykytucxa.entities.Notification;
import com.ktx.quanlykytucxa.entities.NotificationRead;
import com.ktx.quanlykytucxa.repositories.NotificationReadRepository;
import com.ktx.quanlykytucxa.repositories.NotificationRepository;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationReadRepository notificationReadRepository;

    private long readerUserId(String role, Long userId) {
        if ("ADMIN".equals(role)) return 0L;
        if ("STUDENT".equals(role) && userId != null) return userId;
        return -1L;
    }

    @Override
    public ResponseEntity<List<Notification>> getAll(String role, Long userId) {
        if (role == null && userId == null) {
            return ResponseEntity.ok(notificationRepository.findAllByOrderByCreateDateDesc());
        }

        List<Notification> all = notificationRepository.findAllByOrderByCreateDateDesc();
        List<Notification> filtered = all.stream().filter(n -> {
            if ("ALL".equals(n.getTargetRole())) return true;
            if (n.getTargetUserId() != null) {
                return userId != null && userId.equals(n.getTargetUserId());
            }
            return role != null && role.equals(n.getTargetRole());
        }).toList();

        long readerId = readerUserId(role, userId);
        if (readerId >= 0) {
            Set<Long> notifIds = filtered.stream().map(Notification::getId).collect(Collectors.toSet());
            List<NotificationRead> reads = notificationReadRepository.findByReaderRoleAndReaderUserIdAndNotificationIdIn(role, readerId, notifIds);
            Set<Long> readIds = reads.stream().map(NotificationRead::getNotificationId).collect(Collectors.toSet());
            filtered.forEach(n -> n.setIsRead(readIds.contains(n.getId())));
        }

        return ResponseEntity.ok(filtered);
    }

    @Override
    public ResponseEntity<?> markAllRead(String role, Long userId) {
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
        List<NotificationRead> existingReads = notificationReadRepository.findByReaderRoleAndReaderUserIdAndNotificationIdIn(role, readerId, notifIds);
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

        if (!toCreate.isEmpty()) notificationReadRepository.saveAll(toCreate);
        return ResponseEntity.ok("Đã đánh dấu " + toCreate.size() + " thông báo là đã đọc");
    }

    @Override
    public ResponseEntity<Long> getUnreadCount(String role, Long userId) {
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
        List<NotificationRead> reads = notificationReadRepository.findByReaderRoleAndReaderUserIdAndNotificationIdIn(role, readerId, notifIds);
        Set<Long> readIds = reads.stream().map(NotificationRead::getNotificationId).collect(Collectors.toSet());
        long unread = matching.stream().filter(n -> !readIds.contains(n.getId())).count();
        return ResponseEntity.ok(unread);
    }

    @Override
    public ResponseEntity<?> createNotification(Notification notification) {
        try {
            notification.setCreateDate(LocalDateTime.now());
            if (notification.getIsRead() == null) notification.setIsRead(false);
            return ResponseEntity.status(HttpStatus.CREATED).body(notificationRepository.save(notification));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo thông báo: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) return ResponseEntity.notFound().build();
        notificationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
