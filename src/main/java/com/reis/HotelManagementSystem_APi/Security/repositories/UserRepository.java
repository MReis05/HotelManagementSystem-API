package com.reis.HotelManagementSystem_APi.Security.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.reis.HotelManagementSystem_APi.Security.entities.User;

public interface UserRepository extends JpaRepository<User, String> {
	
	UserDetails findByLogin(String login);

}
