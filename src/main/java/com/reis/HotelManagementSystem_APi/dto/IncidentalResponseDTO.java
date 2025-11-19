package com.reis.HotelManagementSystem_APi.dto;

import java.time.LocalDateTime;

import com.reis.HotelManagementSystem_APi.entities.Incidental;

public class IncidentalResponseDTO {

	private Long id;
	private String name;
	private Integer quantity;
	private Double price;
	private Double total;
	private LocalDateTime moment;
	
	public IncidentalResponseDTO() {
	}
	
	public IncidentalResponseDTO(Incidental obj) {
		this.id = obj.getId();
		this.name = obj.getName();
		this.quantity = obj.getQuantity();
		this.price = obj.getPrice();
		this.total = obj.totalValue();
		this.moment = obj.getMoment();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Double getPrice() {
		return price;
	}

	public Double getTotal() {
		return total;
	}

	public LocalDateTime getMoment() {
		return moment;
	}
}
