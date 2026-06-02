package com.ktx.quanlykytucxa.repositories;

import com.ktx.quanlykytucxa.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByOrderByCreateDateDesc();
    
    // Finds notifications that are either targeted to a specific role, or to a specific user (or global ALL)
    List<Notification> findByTargetRoleOrTargetUserIdOrderByCreateDateDesc(String targetRole, Long targetUserId);

    // Used to delete all notifications sent to a specific student before deleting the student
    List<Notification> findByTargetUserId(Long targetUserId);
}
