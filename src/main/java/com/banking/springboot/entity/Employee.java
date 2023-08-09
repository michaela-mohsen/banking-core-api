package com.banking.springboot.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Entity
@Data
@Table(name = "employees")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;

	@Column
	@NotNull
	@JsonProperty
	private String firstName;

	@Column
	@NotNull
	@JsonProperty
	private String lastName;

	@Column
	@NotNull
	@JsonProperty
	private String email;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonProperty
	private Date startDate;

	@Column
	@JsonProperty
	private String title;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "branch_id")
	@JsonProperty
	private Branch branch;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "department_id")
	@JsonProperty
	private Department department;

}
