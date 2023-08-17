package com.banking.springboot.dto;

import lombok.Data;
import lombok.Generated;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Generated
public class CustomerDto {

    private Integer id;

    @NotNull(message = "Birth date is required.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Birth date is required.")
    private String birthDate;

    @Length(min = 2, max = 45, message = "First name must be between 2 and 45 characters long.")
    private String firstName;

    @Length(min = 2, max = 45, message = "Last name must be between 2 and 45 characters long.")
    private String lastName;

    @Length(min = 5, max = 45, message = "Address must be between 5 and 45 characters long.")
    private String address;

    @Length(min = 2, max = 45, message = "City must be between 2 and 45 characters long.")
    private String city;

    @NotEmpty(message = "State is required.")
    private String state;

    @NotEmpty(message = "Zip code is required.")
    @Pattern(regexp = "^\\d{5}$", message = "Zip code must be 5 digits long.")
    private String zipCode;

    List<AccountDto> accounts;
}
