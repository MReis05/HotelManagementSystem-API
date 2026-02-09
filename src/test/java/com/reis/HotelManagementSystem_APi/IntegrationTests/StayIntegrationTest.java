package com.reis.HotelManagementSystem_APi.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reis.HotelManagementSystem_APi.dto.IncidentalRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.PaymentRequestDTO;
import com.reis.HotelManagementSystem_APi.dto.StayRequestDTO;
import com.reis.HotelManagementSystem_APi.entities.Address;
import com.reis.HotelManagementSystem_APi.entities.Guest;
import com.reis.HotelManagementSystem_APi.entities.Payment;
import com.reis.HotelManagementSystem_APi.entities.Reservation;
import com.reis.HotelManagementSystem_APi.entities.Room;
import com.reis.HotelManagementSystem_APi.entities.Stay;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.PaymentType;
import com.reis.HotelManagementSystem_APi.entities.enums.ReservationStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomStatus;
import com.reis.HotelManagementSystem_APi.entities.enums.RoomType;
import com.reis.HotelManagementSystem_APi.repositories.GuestRepository;
import com.reis.HotelManagementSystem_APi.repositories.PaymentRepository;
import com.reis.HotelManagementSystem_APi.repositories.ReservationRepository;
import com.reis.HotelManagementSystem_APi.repositories.RoomRepository;
import com.reis.HotelManagementSystem_APi.repositories.StayRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StayIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private StayRepository repository;
	
	@Autowired
	private ReservationRepository reservationRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private GuestRepository guestRepository;
	
	@Autowired 
	private PaymentRepository paymentRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	private Long stayId;
	
	private Long reservationOngoingId;
	
	private Long reservationConfirmedId;
	
	private LocalDateTime checkInDate;
	
	@BeforeEach
	void injectObjects() {
		Guest g1 = new Guest("John Green", "14462660013", "john@gmail.com", "779118298282", LocalDate.of(2003, 1, 05),
				new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		
		Guest g2 = new Guest("Maria Green", "011.180.880-42", "maria@gmail.com", "859901902120", LocalDate.of(2000, 1, 05),
				new Address("05606-100", "São Paulo", "São Paulo", "Morumbi", "blala", 65));
		
		Room r1 = new Room(1, new BigDecimal("190.00"), "Quarto com Ventilador", RoomStatus.DISPONIVEL,
				RoomType.SOLTEIRO);
		
		Room r2 = new Room(2, new BigDecimal("230.00"), "Quarto com Ar-Condicionado", RoomStatus.DISPONIVEL,
				RoomType.CASAL);
		
		Reservation rv = new Reservation(LocalDate.now().minusDays(4L), LocalDate.now(), ReservationStatus.EM_CURSO);
		rv.setTotalValue(new BigDecimal("760.00"));
		rv.setRoom(r1);
		rv.setGuest(g1);
		
		Reservation rv2 = new Reservation(LocalDate.now(), LocalDate.now().plusDays(4L), ReservationStatus.CONFIRMADA);
		rv2.setTotalValue(new BigDecimal("920.00"));
		rv2.setRoom(r2);
		rv2.setGuest(g2);
		
		r1.getReservations().add(rv);
		r2.getReservations().add(rv2);
		
		this.guestRepository.saveAll(Arrays.asList(g1, g2));
		this.roomRepository.saveAll(Arrays.asList(r1, r2));
		
		rv = this.reservationRepository.save(rv);
		rv2 = this.reservationRepository.save(rv2);
		
		Stay stay = new Stay(LocalDateTime.now().minusDays(4L).withNano(0), null, rv);
		
		stay = this.repository.save(stay);
		
		reservationOngoingId = rv.getId();
		reservationConfirmedId = rv2.getId();
		stayId = stay.getId();
		checkInDate = stay.getCheckInDate();
	}
	
	@Test
	@DisplayName("Should return all Stays and 200 OK status (End-to-End")
	void findAllSuccessCase() throws Exception {
		mockMvc.perform(
				get("/stays")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(stayId))
				.andExpect(jsonPath("$[0].checkInDate").value(checkInDate.toString()))
				.andExpect(jsonPath("$[0].totalValue").value(760.00))
				.andExpect(jsonPath("$[0].guestSummaryDTO.name").value("John Green"));
	}
	
	@Test
	@DisplayName("Should return Stay when finding by existing Id and 200 Ok status (End-to-End)")
	void findByIdSuccessCase() throws Exception {
		mockMvc.perform(
				get("/stays/{id}", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(stayId))
				.andExpect(jsonPath("$.checkInDate").value(checkInDate.toString()))
				.andExpect(jsonPath("$.totalValue").value(760.00))
				.andExpect(jsonPath("$.reservationId").value(reservationOngoingId))
				.andExpect(jsonPath("$.guestSummaryDTO.name").value("John Green"))
				.andExpect(jsonPath("$.incidentalsList").isArray());	
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found when doesn't find Stay")
	void findByIdResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/stays/{id}", (stayId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (stayId + 98)));
	}
	
	@Test
	@DisplayName("Should make Check-In and return 201 Created (End-to-End)")
	void checkInSuccessCase() throws Exception {
		StayRequestDTO inputDTO = new StayRequestDTO(reservationConfirmedId, LocalDateTime.now().withNano(0), null);
		LocalDateTime inputDate = inputDTO.getCheckInDate();
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.checkInDate").value(inputDate.toString()))
				.andExpect(jsonPath("$.totalValue").value(920.00))
				.andExpect(jsonPath("$.reservationId").value(reservationConfirmedId))
				.andExpect(jsonPath("$.guestSummaryDTO.name").value("Maria Green"))
				.andExpect(jsonPath("$.incidentalsList").isArray())
				.andExpect(header().exists("Location"));
		
		Stay staySaved = repository.findAll().stream().filter(s -> s.getReservation().getId().equals(reservationConfirmedId))
					.findFirst().orElseThrow(() -> new AssertionError("Stay não encontrada"));
		
		assertEquals(2, repository.count());
		assertEquals(inputDate, staySaved.getCheckInDate());
		assertNull(staySaved.getCheckOutDate());
		assertEquals(reservationConfirmedId, staySaved.getReservation().getId());
		assertEquals("Maria Green", staySaved.getReservation().getGuest().getName());
		assertTrue(staySaved.getIncidentals().isEmpty());
		assertEquals(new BigDecimal("920.00"), staySaved.getStayTotalValue());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Reservation")
	void insertResourceNotFoundCase() throws Exception {
		StayRequestDTO inputDTO = new StayRequestDTO((reservationConfirmedId + 98), LocalDateTime.now().withNano(0), null);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (reservationConfirmedId + 98)));
		
		assertEquals(1, repository.count());
	}
	
	@Test
	@DisplayName("Should throw a CheckInDateException and 400 Bad Request status when CheckInDates don't matche")
	void insertConflitDatesCase() throws Exception {
		Reservation rv2 = reservationRepository.findById(reservationConfirmedId).orElseThrow();
		rv2.setCheckInDate(LocalDate.now().plusDays(1));
		reservationRepository.save(rv2);
		
		StayRequestDTO inputDTO = new StayRequestDTO(reservationConfirmedId, LocalDateTime.now().withNano(0), null);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Invalid Check-In Date"))
				.andExpect(jsonPath("$.message").value("A data de Check-In informada é diferente da data informada na reserva"
														+ ". Data de Check-In na reserva: " + rv2.getCheckInDate().toString()
														+ ". Data de Check-In na estadia: " + inputDTO.getCheckInDate().toString()));
		
		assertEquals(1, repository.count());
				
	}
	
	@Test
	@DisplayName("Should compare payments with bill and make CheckOut (End-to-End)")
	void checkOutSucessCase() throws Exception {
		Reservation reservation = reservationRepository.findById(reservationOngoingId).orElseThrow();
		
		Payment payment = new Payment(Instant.now(), PaymentStatus.APROVADO, PaymentType.CARTAO_DE_CREDITO, new BigDecimal("760.00"), reservation);
		paymentRepository.save(payment);
		reservation.getPayments().add(payment);
		reservationRepository.save(reservation);
		
		mockMvc.perform(
				patch("/stays/{id}/checkout", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(stayId))
				.andExpect(jsonPath("$.checkInDate").value(checkInDate.toString()))
				.andExpect(jsonPath("$.checkOutDate").isNotEmpty())
				.andExpect(jsonPath("$.totalValue").value(760.00))
				.andExpect(jsonPath("$.reservationId").value(reservationOngoingId))
				.andExpect(jsonPath("$.guestSummaryDTO.name").value("John Green"))
				.andExpect(jsonPath("$.incidentalsList").isArray());
		
		Stay staySaved = repository.findById(stayId).orElseThrow();
		
		assertEquals(1, repository.count());
		assertEquals(checkInDate, staySaved.getCheckInDate());
		assertNotNull(staySaved.getCheckOutDate());
		assertEquals(reservationOngoingId, staySaved.getReservation().getId());
		assertEquals("John Green", staySaved.getReservation().getGuest().getName());
		assertEquals(ReservationStatus.CONCLUIDA, staySaved.getReservation().getStatus());
		assertEquals(RoomStatus.LIMPEZA, staySaved.getReservation().getRoom().getStatus());
		assertTrue(staySaved.getIncidentals().isEmpty());
		assertEquals(new BigDecimal("760.00"), staySaved.getStayTotalValue());
		assertEquals(1, staySaved.getReservation().getPayments().size());
		assertEquals(new BigDecimal("760.00"), staySaved.getReservation().getPayments().get(0).getAmount());
		assertEquals(PaymentStatus.APROVADO, staySaved.getReservation().getPayments().get(0).getStatus());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn't find Stay")
	void checkOutResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				patch("/stays/{id}/checkout", (stayId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (stayId + 98)));
		
		Stay staySaved = repository.findById(stayId).orElseThrow();
		
		assertEquals(1, repository.count());
		assertNull(staySaved.getCheckOutDate());
	}
	
	@Test
	@DisplayName("Should throw an InvalidActionException and 400 Bad Request status when Stay is already completed")
	void checkOutInvalidActionCase() throws Exception {
		Stay stay = repository.findById(stayId).orElseThrow();
		stay.setCheckOutDate(LocalDateTime.now().withNano(0));
		repository.save(stay);
		
		mockMvc.perform(
				patch("/stays/{id}/checkout", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Invalid Action"))
				.andExpect(jsonPath("$.message").value("Estadia já foi finalizada"));
		
		assertEquals(1, repository.count());
	}
	
	@Test
	@DisplayName("Should throw a CheckOutException when the bill has not yet been paid in full")
	void checkOutExceptionCase() throws Exception {
		mockMvc.perform(
				patch("/stays/{id}/checkout", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Error making Check-Out"))
				.andExpect(jsonPath("$.message").exists());
		
		entityManager.clear();
		
		Stay staySaved = repository.findById(stayId).orElseThrow();
		
		assertEquals(1, repository.count());
		assertNull(staySaved.getCheckOutDate());
	}
	
	@Test
	@DisplayName("Should create and add inicidental in Stay and return 201 Created (End-to-End)")
	void addInicidentalSuccessCase() throws Exception {
		IncidentalRequestDTO inputDTO = new IncidentalRequestDTO("Coca-Cola", 2, new BigDecimal("5.00"), LocalDateTime.now().minusDays(1).withNano(0));
		
		LocalDateTime moment = inputDTO.getMoment();
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/incidental", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("Coca-Cola"))
				.andExpect(jsonPath("$.quantity").value(2))
				.andExpect(jsonPath("$.price").value(5.00))
				.andExpect(jsonPath("$.total").value(10.00))
				.andExpect(jsonPath("$.moment").value(moment.toString()))
				.andExpect(header().exists("Location"));
		
		Stay staySaved = repository.findById(stayId).orElseThrow();
		
		assertEquals(1, repository.count());
		assertEquals(new BigDecimal("770.00"), staySaved.getStayTotalValue());
		assertEquals("Coca-Cola", staySaved.getIncidentals().get(0).getName());
		assertEquals(2, staySaved.getIncidentals().get(0).getQuantity());
		assertEquals(new BigDecimal("5.00"), staySaved.getIncidentals().get(0).getPrice());
		assertEquals(moment, staySaved.getIncidentals().get(0).getMoment());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not Found status when doesn's find Stay (End-to-End)")
	void addInicidentalResourceNotFoundCase() throws Exception {
		IncidentalRequestDTO inputDTO = new IncidentalRequestDTO("Coca-Cola", 2, new BigDecimal("5.00"), LocalDateTime.now().minusDays(1).withNano(0));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/incidental", (stayId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (stayId + 98)));

		Stay staySaved = repository.findById(stayId).orElseThrow();
		
		assertEquals(1, repository.count());
		assertEquals(new BigDecimal("760.00"), staySaved.getStayTotalValue());
		assertTrue(staySaved.getIncidentals().isEmpty());
	}
	
	@Test
	@DisplayName("Should throw an InvalidActionException and 400 Bad Request status when Stay is already completed")
	void addInicidentalInvalidActionCase() throws Exception {
		Stay stay = repository.findById(stayId).orElseThrow();
		stay.setCheckOutDate(LocalDateTime.now().withNano(0));
		repository.save(stay);
		
		IncidentalRequestDTO inputDTO = new IncidentalRequestDTO("Coca-Cola", 2, new BigDecimal("5.00"), LocalDateTime.now().minusDays(1).withNano(0));
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/incidental", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Invalid Action"))
				.andExpect(jsonPath("$.message").value("Não é possivel adicionar um consumo em uma estadia finalizada"));
		
		Stay staySaved = repository.findById(stayId).orElseThrow();
		
		assertEquals(1, repository.count());
		assertEquals(new BigDecimal("760.00"), staySaved.getStayTotalValue());
		assertTrue(staySaved.getIncidentals().isEmpty());
	}
	
	@Test
	@DisplayName("Should create and Payment in Stay and return 201 created (End-to-End)")
	void makePaymentSuccessCase() throws Exception {
		PaymentRequestDTO inputDTO = new PaymentRequestDTO(PaymentType.PIX, new BigDecimal("300.000"), reservationOngoingId);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/payment", stayId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.moment").exists())
				.andExpect(jsonPath("$.amount").value(300.00))
				.andExpect(jsonPath("$.status").value(PaymentStatus.APROVADO.name()))
				.andExpect(jsonPath("$.type").value(PaymentType.PIX.name()))
				.andExpect(jsonPath("$.reservationId").value(reservationOngoingId))
				.andExpect(header().exists("Location"));
		
		Stay staySaved = repository.findById(stayId).orElseThrow();
		
		assertEquals(1, repository.count());
		assertEquals(new BigDecimal("300.00"), staySaved.getReservation().getPayments().get(0).getAmount());
		assertEquals(PaymentStatus.APROVADO, staySaved.getReservation().getPayments().get(0).getStatus());
		assertEquals(PaymentType.PIX, staySaved.getReservation().getPayments().get(0).getType());
		assertNotNull(staySaved.getReservation().getPayments().get(0).getMoment());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFound Exception when doesn't find Stay")
	void makePaymentResourceNotFoundCase() throws Exception {
		PaymentRequestDTO inputDTO = new PaymentRequestDTO(PaymentType.PIX, new BigDecimal("300.000"), reservationOngoingId);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/stays/{id}/payment", (stayId + 98))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (stayId + 98)));
		
		Stay staySaved = repository.findById(stayId).orElseThrow();
		
		assertEquals(1, repository.count());
		assertTrue(staySaved.getReservation().getPayments().isEmpty());
	}
}
