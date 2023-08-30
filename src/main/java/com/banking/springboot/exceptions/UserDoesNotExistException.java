package com.banking.springboot.exceptions;

public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(String s) {
        super(s);
    }
}
