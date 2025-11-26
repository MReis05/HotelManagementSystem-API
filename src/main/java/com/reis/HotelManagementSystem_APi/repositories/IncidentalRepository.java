package com.reis.HotelManagementSystem_APi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.HotelManagementSystem_APi.entities.Incidental;

public interface IncidentalRepository extends JpaRepository<Incidental, Long> {
	
}
