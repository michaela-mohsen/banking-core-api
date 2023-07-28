package com.banking.springboot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomerDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomerDoesNotExistException handleCustomerDoesNotExistException(CustomerDoesNotExistException e) {
        return e;
    }

    @ExceptionHandler(AccountDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AccountDoesNotExistException handleAccountDoesNotExistException(AccountDoesNotExistException e) {
        return e;
    }

    @ExceptionHandler(BranchDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BranchDoesNotExistException handleBranchDoesNotExistException(BranchDoesNotExistException e) {
        return e;
    }
}
