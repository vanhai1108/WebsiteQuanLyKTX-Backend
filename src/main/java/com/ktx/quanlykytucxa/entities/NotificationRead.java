package com.ktx.quanlykytucxa.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notification_reads",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_notification_reader",
                columnNames = {"notification_id", "reader_role", "reader_user_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @Column(name = "reader_role", nullable = false)
    private String readerRole; // "ADMIN" or "STUDENT"

    @Column(name = "reader_user_id", nullable = false)
    private Long readerUserId; // STUDENT: studentId, ADMIN: 0

    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;
}

