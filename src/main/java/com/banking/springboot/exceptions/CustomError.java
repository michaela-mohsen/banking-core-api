package com.banking.springboot.exceptions;

import lombok.Data;

@Data
public class CustomError {
    private String field;
    private String message;
}
