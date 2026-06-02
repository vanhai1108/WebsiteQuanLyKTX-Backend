package com.ktx.quanlykytucxa.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ktx.quanlykytucxa.entities.Room;
import com.ktx.quanlykytucxa.repositories.RoomRepository;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public Room createRoom(Room room) {
        // Có thể thêm logic kiểm tra trùng mã phòng ở đây
        if (roomRepository.existsByRoomCode(room.getRoomCode())) {
            throw new RuntimeException("Mã phòng đã tồn tại!");
        }
        if (room.getLocked() == null) {
            room.setLocked(false);
        }
        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Long id, Room roomDetails) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));

        room.setRoomCode(roomDetails.getRoomCode());
        room.setBuilding(roomDetails.getBuilding());
        room.setMaxCapacity(roomDetails.getMaxCapacity());
        room.setCurrentCapacity(roomDetails.getCurrentCapacity());
        room.setPrice(roomDetails.getPrice());

        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));
        roomRepository.delete(room);
    }

    @Override
    public Room lockRoom(Long id, String reason) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));
        room.setLocked(true);
        room.setLockReason(reason);
        return roomRepository.save(room);
    }

    @Override
    public Room unlockRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));
        room.setLocked(false);
        room.setLockReason(null);
        return roomRepository.save(room);
    }
}
