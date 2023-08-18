package com.banking.springboot.exceptions;

public class EmployeeAlreadyExistsException extends Exception {

    public EmployeeAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }

    public EmployeeAlreadyExistsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public EmployeeAlreadyExistsException() {
        super();
    }
}
