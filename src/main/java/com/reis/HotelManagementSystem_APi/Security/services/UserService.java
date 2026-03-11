package com.reis.HotelManagementSystem_APi.Security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.reis.HotelManagementSystem_APi.Security.dto.RegisterDTO;
import com.reis.HotelManagementSystem_APi.Security.dto.UserResponseDTO;
import com.reis.HotelManagementSystem_APi.Security.entities.User;
import com.reis.HotelManagementSystem_APi.Security.repositories.UserRepository;
import com.reis.HotelManagementSystem_APi.Security.services.exceptions.ExistedAccountException;

import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	
	@Transactional
	public UserResponseDTO Register (RegisterDTO dto) {
		if(userRepository.findByLogin(dto.getLogin()) != null) {
			throw new ExistedAccountException();
		}
		
		String encryptedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());
		User user = new User(dto.getLogin(), encryptedPassword, dto.getUserRole());
		
		user = userRepository.save(user);
		
		return new UserResponseDTO(user);
	}
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByLogin(username);
	}

}
