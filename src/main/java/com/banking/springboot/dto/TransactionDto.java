package com.banking.springboot.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class TransactionDto {

    @NotNull(message = "Amount is required.")
    @Positive(message = "Amount must be positive.")
    @Max(value = 1000000, message = "Amount must be less than or equal to $1,000,000.")
    @Min(value = 1, message = "Amount must be at least $1.")
    private Double amount;

    private String fundsAvailableDate;

    private String date;

    private String time;

    @NotEmpty(message = "Transaction type is required.")
    private String type;

    @NotNull(message = "Account number is required.")
    private Integer account;

}
