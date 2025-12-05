package com.reis.HotelManagementSystem_APi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.reis.HotelManagementSystem_APi.entities.Incidental;

public class IncidentalResponseDTO {

	private Long id;
	private String name;
	private Integer quantity;
	private BigDecimal price;
	private BigDecimal total;
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

	public BigDecimal getPrice() {
		return price;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public LocalDateTime getMoment() {
		return moment;
	}
}
