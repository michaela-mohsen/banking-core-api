package com.banking.springboot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "customers")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	@JsonProperty
	private LocalDate birthDate;

	@Column
	@JsonProperty
	private String firstName;

	@Column
	@JsonProperty
	private String lastName;

	@Column
	@JsonProperty
	private String address;

	@Column
	@JsonProperty
	private String city;

	@Column
	@JsonProperty
	private String state;

	@Column
	@JsonProperty
	private String zipCode;

}
