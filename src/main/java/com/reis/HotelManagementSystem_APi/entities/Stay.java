package com.reis.HotelManagementSystem_APi.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_stay")
public class Stay implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime checkInDate;
	private LocalDateTime checkOutDate;
	
	@OneToOne
	@JoinColumn(name = "reservation_id", nullable = false)
	private Reservation reservation;
	
	@OneToMany(mappedBy = "stay")
	private List<Incidental> incidental = new ArrayList<>();
	
	public Stay() {
	}

	public Stay(LocalDateTime checkInDate, LocalDateTime checkOutDate, Reservation reservation) {
		super();
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.reservation = reservation;
	}

	public LocalDateTime getCheckInDate() {
		return checkInDate;
	}

	public void setCheckInDate(LocalDateTime checkInDate) {
		this.checkInDate = checkInDate;
	}

	public LocalDateTime getCheckOutDate() {
		return checkOutDate;
	}

	public void setCheckOutDate(LocalDateTime checkOutDate) {
		this.checkOutDate = checkOutDate;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public Long getId() {
		return id;
	}

	public List<Incidental> getIncidental() {
		return incidental;
	}
	
	public Double getIncidentalTotalValue() {
		if(incidental == null || incidental.isEmpty()) {
			return 0.00;
		}
		
		return incidental.stream().mapToDouble(Incidental::totalValue).sum();
	}
	
	public Double getStayTotalValue() {
		Double totalValue = 0.00;
		if(reservation != null && reservation.getTotalValue() != null) {
			totalValue = reservation.getTotalValue();
		}
		
		Double totalIncidental = getIncidentalTotalValue();
		
		return totalValue + totalIncidental;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stay other = (Stay) obj;
		return Objects.equals(id, other.id);
	}
}
