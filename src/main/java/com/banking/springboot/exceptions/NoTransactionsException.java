package com.banking.springboot.exceptions;

public class NoTransactionsException extends Exception {
    public NoTransactionsException(String errorMessage) {
        super(errorMessage);
    }
}
