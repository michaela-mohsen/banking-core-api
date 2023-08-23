package com.banking.springboot.exceptions;

public class EmployeeDoesNotExistException extends Exception {
    public EmployeeDoesNotExistException(String s) {
        super(s);
    }
}
