package com.reis.HotelManagementSystem_APi.config;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private GuestRepository guestRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Override
	public void run(String... args) throws Exception {
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		Guest g2 = new Guest("Maria White", "99999999901","maria@gmail.com", "779118298282", LocalDate.of(2000, 5, 12), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		Guest g3 = new Guest("Clark Brown", "99999999901","clark@gmail.com", "779118298282", LocalDate.of(1978, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		
		Room r1 = new Room(01, 190.00, "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		Room r2 = new Room(02, 240.00, "Quarto com Ar-Condicionado", RoomStatus.LIMPEZA, RoomType.CASAL);
		Room r3 = new Room(03, 340.00, "Quarto com Vararanda", RoomStatus.OCUPADO, RoomType.TRIPLO);
		
		roomRepository.saveAll(Arrays.asList(r1, r2, r3));
		guestRepository.saveAll(Arrays.asList(g1, g2, g3));
	}

}
