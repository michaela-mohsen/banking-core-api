package com.banking.springboot.entity;

import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.banking.springboot.auth.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Entity
@Data
@Table(name = "employees")
@Generated
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
	@JsonProperty
	private LocalDate startDate;

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

	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name = "user_id")
	private User user;
}
