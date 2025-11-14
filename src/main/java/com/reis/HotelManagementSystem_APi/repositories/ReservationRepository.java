package com.reis.HotelManagementSystem_APi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	List<Reservation> findByStatus (ReservationStatus status);
}
