package com.reis.HotelManagementSystem_APi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;

public interface RoomRepository extends JpaRepository<Room, Long> {
	
	List<Room> findByStatus(RoomStatus status);
	List<Room> findByType(RoomType type);
	List<Room> findByTypeAndStatus(RoomType type, RoomStatus status);
}
