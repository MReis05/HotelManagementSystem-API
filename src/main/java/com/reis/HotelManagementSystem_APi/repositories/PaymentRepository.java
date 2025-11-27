package com.reis.HotelManagementSystem_APi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.HotelManagementSystem_APi.entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
