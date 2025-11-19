package com.reis.HotelManagementSystem_APi.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class IncidentalRequestDTO {

	@NotBlank
	private String name;
	@NotNull
	@Positive
	private Integer quantity;
	@NotNull
	@Positive
	private Double price;
	private LocalDateTime moment;
	
	public IncidentalRequestDTO() {
	}

	public IncidentalRequestDTO(String name, Integer quantity, Double price, LocalDateTime moment) {
		super();
		this.name = name;
		this.quantity = quantity;
		this.price = price;
		this.moment = moment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public LocalDateTime getMoment() {
		return moment;
	}

	public void setMoment(LocalDateTime moment) {
		this.moment = moment;
	}
}
