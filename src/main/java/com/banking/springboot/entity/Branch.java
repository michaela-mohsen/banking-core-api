package com.banking.springboot.entity;

 import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
	@JsonProperty
	private String name;

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
