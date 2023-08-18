package com.banking.springboot.exceptions;

public class DepartmentDoesNotExistException extends Exception {
    public DepartmentDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
