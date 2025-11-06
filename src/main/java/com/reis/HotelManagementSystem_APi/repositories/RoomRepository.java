package com.reis.HotelManagementSystem_APi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.HotelManagementSystem_APi.entities.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
