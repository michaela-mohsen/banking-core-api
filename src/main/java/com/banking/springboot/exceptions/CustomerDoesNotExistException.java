package com.banking.springboot.exceptions;

public class CustomerDoesNotExistException extends Exception {

    public CustomerDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }

    public CustomerDoesNotExistException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public CustomerDoesNotExistException() {
        super();
    }
}
