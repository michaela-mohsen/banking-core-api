package com.banking.springboot.dto;

import lombok.Data;
import lombok.Generated;

import javax.validation.constraints.NotEmpty;

@Data
@Generated
public class LoginDto {

    @NotEmpty(message = "Username is required.")
    private String username;

    @NotEmpty(message = "Password is required.")
    private String password;
}
