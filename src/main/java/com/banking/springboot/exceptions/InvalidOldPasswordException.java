package com.banking.springboot.exceptions;

public class InvalidOldPasswordException extends Exception {
    public InvalidOldPasswordException(String errorMessage) {
        super(errorMessage);
    }
}
