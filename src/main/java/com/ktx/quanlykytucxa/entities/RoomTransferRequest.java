package com.ktx.quanlykytucxa.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_transfer_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTransferRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "current_room_id", nullable = false)
    private Room currentRoom;

    @ManyToOne
    @JoinColumn(name = "requested_room_id", nullable = false)
    private Room requestedRoom;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Builder.Default
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    private String rejectReason;

    private LocalDateTime requestDate;
}
