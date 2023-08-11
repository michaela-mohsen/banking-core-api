package com.banking.springboot.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Entity
@Data
@Table(name = "transactions")
@Generated
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	@JsonProperty
	private Double amount;

	@Column
	@JsonProperty
	private LocalDate fundsAvailableDate;

	@Column(updatable = false)
	@JsonProperty
	private LocalDateTime date;

	@Column
	@JsonProperty
	private String type;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "account_id")
	@JsonProperty
	private Account account;
}
