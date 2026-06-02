package com.ktx.quanlykytucxa.repositories;

import com.ktx.quanlykytucxa.entities.NotificationRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {
    List<NotificationRead> findByReaderRoleAndReaderUserIdAndNotificationIdIn(
            String readerRole,
            Long readerUserId,
            Collection<Long> notificationIds
    );

    boolean existsByNotificationIdAndReaderRoleAndReaderUserId(Long notificationId, String readerRole, Long readerUserId);
}

