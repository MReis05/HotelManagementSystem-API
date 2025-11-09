package com.reis.HotelManagementSystem_APi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.HotelManagementSystem_APi.entities.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
