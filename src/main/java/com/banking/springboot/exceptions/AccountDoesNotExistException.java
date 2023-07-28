package com.banking.springboot.exceptions;

public class AccountDoesNotExistException extends Exception {
    public AccountDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }

    public AccountDoesNotExistException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
