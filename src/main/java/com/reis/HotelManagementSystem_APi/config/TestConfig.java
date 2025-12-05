package com.reis.HotelManagementSystem_APi.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.Stay;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.repositories.StayRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private GuestRepository guestRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
	private StayRepository stayRepository;
	
	@Override
	public void run(String... args) throws Exception {
		Guest g1 = new Guest("John Green", "99999999901","john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		Guest g2 = new Guest("Maria White", "99999999901","maria@gmail.com", "779118298282", LocalDate.of(2000, 5, 12), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		Guest g3 = new Guest("Clark Brown", "99999999901","clark@gmail.com", "779118298282", LocalDate.of(1978, 1, 05), new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL, RoomType.SOLTEIRO);
		Room r2 = new Room(2, new BigDecimal("240.00"), "Quarto com Ar-Condicionado", RoomStatus.LIMPEZA, RoomType.CASAL);
		Room r3 = new Room(3, new BigDecimal("340.00"), "Quarto com Vararanda", RoomStatus.OCUPADO, RoomType.TRIPLO);
		
		Reservation rv1 = new Reservation(LocalDate.of(2025, 11, 25), LocalDate.of(2025, 11, 28), ReservationStatus.CONFIRMADA);
		rv1.setGuest(g1);
		rv1.setRoom(r1);
		Reservation rv2 = new Reservation(LocalDate.of(2025, 11, 8), LocalDate.of(2025, 11, 10), ReservationStatus.CANCELADA);
		rv2.setGuest(g2);
		rv2.setRoom(r2);
		Reservation rv3 = new Reservation(LocalDate.of(2025, 11, 11), LocalDate.of(2025, 11, 15), ReservationStatus.CONCLUIDA);
		rv3.setGuest(g3);
		rv3.setRoom(r3);
		
		Stay s1 = new Stay(LocalDateTime.of(2025, 11, 25, 13, 00), null, rv1);
		Stay s2 = new Stay(LocalDateTime.of(2025, 11, 8, 13, 00), LocalDateTime.of(2025, 11, 10, 13, 00), rv3);
		
		
		roomRepository.saveAll(Arrays.asList(r1, r2, r3));
		guestRepository.saveAll(Arrays.asList(g1, g2, g3));
		reservationRepository.saveAll(Arrays.asList(rv1, rv2, rv3));
		stayRepository.saveAll(Arrays.asList(s1, s2));
	}

}
