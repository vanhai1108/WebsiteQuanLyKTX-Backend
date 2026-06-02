package com.ktx.quanlykytucxa.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime createDate;

    private String targetRole; // "ALL", "ADMIN", "STUDENT"
    private Long targetUserId; // specific student id (matches student.getId())

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false; // tracks whether the target has viewed this notification
}
