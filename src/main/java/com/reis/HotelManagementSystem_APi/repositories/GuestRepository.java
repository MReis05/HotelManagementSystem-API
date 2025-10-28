package com.reis.HotelManagementSystem_APi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.HotelManagementSystem_APi.entities.Guest;

public interface GuestRepository extends JpaRepository<Guest, Long> {

}
