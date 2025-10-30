package com.reis.HotelManagementSystem_APi.entities.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public class GuestRequestDTO {

	@NotBlank
	private String name;
	@CPF
	@NotBlank
	private String cpf;
	@Email
	@NotBlank
	private String email;
	private String phone;
	@NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve estar no passado.")
    @JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate birthDate;
	
	@Valid
	@NotNull
	private AddressDTO address;
	
	public GuestRequestDTO() {
	}
	
	public GuestRequestDTO (String name, String cpf, String email, String phone, LocalDate birthDate, AddressDTO address) {
		super();
		this.name = name;
		this.cpf = cpf;
		this.email = email;
		this.phone = phone;
		this.birthDate = birthDate;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public AddressDTO getAddress() {
		return address;
	}

	public void setAddress(AddressDTO address) {
		this.address = address;
	}
}
