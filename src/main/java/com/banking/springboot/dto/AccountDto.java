package com.banking.springboot.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;

@Data
public class AccountDto {

    private Integer id;

    @NotNull(message = "Available balance is required.")
    @Positive(message = "Available balance must be positive.")
    @Max(value = 1000000, message = "Available balance must be less than or equal to $1,000,000.")
    @Min(value = 1, message = "Available balance must be at least $1.")
    private Double availableBalance;

    @NotNull(message = "Pending balance is required.")
    @Positive(message = "Pending balance must be positive.")
    @Max(value = 1000000, message = "Pending balance must be less than or equal to $1,000,000.")
    @Min(value = 1, message = "Pending balance must be at least $1.")
    private Double pendingBalance;

    private String active;

    @NotEmpty(message = "Birth date is required.")
    private String birthDate;

    @NotEmpty(message = "Last name is required.")
    private String lastName;

    @NotEmpty(message = "Branch name is required.")
    private String branch;

    @NotNull(message = "Employee ID is required.")
    private Integer employee;

    @NotEmpty(message = "Product type is required.")
    private String product;

    private String lastActivityDate;

    private String openDate;

    private List<TransactionDto> transactions;
}
