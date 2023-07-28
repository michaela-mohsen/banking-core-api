package com.banking.springboot.exceptions;

public class BranchDoesNotExistException extends Exception{
    public BranchDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
