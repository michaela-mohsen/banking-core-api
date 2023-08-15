package com.banking.springboot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "accounts")
@Generated
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	@JsonProperty
	private Double availableBalance;

	@Column
	@JsonProperty
	private LocalDateTime lastActivityDate;

	@Column
	@JsonProperty
	private LocalDate openDate;

	@Column
	@JsonProperty
	private Double pendingBalance;

	@Column
	@JsonProperty
	private Boolean active;

	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name = "customer")
	@JsonProperty
	private Customer customer;

	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name = "branch")
	@JsonProperty
	private Branch branch;

	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name = "employee")
	@JsonProperty
	private Employee employee;

	@ManyToOne(cascade = {CascadeType.MERGE})
	@JoinColumn(name = "product")
	@JsonProperty
	private Product product;

}
