package com.banking.springboot.security.jwt;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TokenRefreshRequest {
    @NotBlank
    private String refreshToken;
}
