package com.ktx.quanlykytucxa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ktx.quanlykytucxa.entities.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomCode(String roomCode);
    boolean existsByRoomCode(String roomCode);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.currentCapacity < r.maxCapacity AND r.locked = false")
    long countAvailableRooms();

    long countByLocked(Boolean locked);
}
