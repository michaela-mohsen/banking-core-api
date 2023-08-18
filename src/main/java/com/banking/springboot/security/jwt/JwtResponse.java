package com.banking.springboot.security.jwt;

import lombok.Data;
import lombok.Generated;

import java.util.List;

@Data
@Generated
public class JwtResponse {
    private Integer id;
    private String token;
    private String type;
    private String username;
    private String email;
    private List<String> roles;
}
