package com.banking.springboot.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CustomerDto {

    private Integer id;

    @NotNull(message = "Birth date is required.")
    private String birthDate;

    @NotEmpty(message = "First name is required.")
    @Length(min = 2, max = 45, message = "First name must be between 2 and 45 characters long.")
    private String firstName;

    @NotEmpty(message = "Last name is required.")
    @Length(min = 2, max = 45, message = "Last name must be between 2 and 45 characters long.")
    private String lastName;

    @NotEmpty(message = "Address is required.")
    @Length(min = 5, max = 45, message = "Address must be between 5 and 45 characters long.")
    private String address;

    @NotEmpty(message = "City is required.")
    @Length(min = 2, max = 45, message = "City must be between 2 and 45 characters long.")
    private String city;

    @NotEmpty(message = "State is required.")
    private String state;

    @NotEmpty(message = "Zip code is required.")
    @Pattern(regexp = "^\\d{5}$", message = "Zip code must be 5 digits long.")
    private String zipCode;
}
