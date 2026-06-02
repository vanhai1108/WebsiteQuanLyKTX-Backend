package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Optional;

import com.ktx.quanlykytucxa.entities.Room;

public interface RoomService {
    List<Room> getAllRooms();
    Optional<Room> getRoomById(Long id);
    Room createRoom(Room room);
    Room updateRoom(Long id, Room roomDetails);
    void deleteRoom(Long id);
    Room lockRoom(Long id, String reason);
    Room unlockRoom(Long id);
}
