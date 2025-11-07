package com.reis.HotelManagementSystem_APi.dto;

import java.time.LocalDate;

import com.reis.HotelManagementSystem_APi.entities.Guest;

public class GuestResponseDTO {

	private Long id;
	private String name;
	private String cpf;
	private String email;
	private String phone;
	private LocalDate birthDate;
	
	private AddressDTO address;
	
	public GuestResponseDTO() {
	}
	
	public GuestResponseDTO (Guest obj) {
		super();
		this.id = obj.getId();
		this.name = obj.getName();
		this.cpf = obj.getCpf();
		this.email = obj.getEmail();
		this.phone = obj.getPhone();
		this.birthDate = obj.getBirthDate();
		this.address = new AddressDTO(obj.getAddress().getCep(), obj.getAddress().getUf(), obj.getAddress().getCity(), obj.getAddress().getNeighborhood(), obj.getAddress().getStreet(), obj.getAddress().getHouseNumber());
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCpf() {
		return cpf;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public AddressDTO getAddress() {
		return address;
	}
}
