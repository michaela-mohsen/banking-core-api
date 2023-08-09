package com.banking.springboot.entity;

 import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Entity
@Data
@Table(name = "branches")
public class Branch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	@NotNull
	@JsonProperty
	private String name;

	@Column
	@NotNull
	@JsonProperty
	private String address;

	@Column
	@NotNull
	@JsonProperty
	private String city;

	@Column
	@NotNull
	@JsonProperty
	private String state;

	@Column
	@NotNull
	@JsonProperty
	private String zipCode;

}
