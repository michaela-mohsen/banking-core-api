package com.banking.springboot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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

	@OneToMany(mappedBy = "customer", cascade = CascadeType.MERGE)
	@JsonIgnore
	private List<Account> accounts;

}
