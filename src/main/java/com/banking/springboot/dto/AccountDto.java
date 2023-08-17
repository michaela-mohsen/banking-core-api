package com.banking.springboot.dto;

import lombok.Data;
import lombok.Generated;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.List;

@Data
@Generated
public class AccountDto {

    private Integer id;

    @NotNull(message = "Available balance is required.")
    @Max(value = 1000000, message = "Available balance must be less than or equal to $1,000,000.")
    private Double availableBalance;

    @NotNull(message = "Pending balance is required.")
    @Max(value = 1000000, message = "Pending balance must be less than or equal to $1,000,000.")
    private Double pendingBalance;

    private String active;

    @NotEmpty(message = "Birth date is required.")
    private String birthDate;

    @Length(min = 2, max = 45, message = "Last name must be between 2 and 45 characters long.")
    private String lastName;

    @NotEmpty(message = "Branch name is required.")
    private String branch;

    @NotNull(message = "Employee ID is required.")
    @Min(message = "Invalid employee ID.", value = 1)
    private Integer employee;

    @NotEmpty(message = "Product type is required.")
    private String product;

    private String lastActivityDate;

    private String openDate;

    private List<TransactionDto> transactions;
}
