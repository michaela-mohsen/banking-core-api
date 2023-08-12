package com.banking.springboot.controller;

import com.banking.springboot.dto.TransactionDto;
import com.banking.springboot.entity.Transaction;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.CustomError;
import com.banking.springboot.service.impl.TransactionServiceImpl;
import com.banking.springboot.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin("http://localhost:3000")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionServiceImpl service;

    @Autowired
    private Utility util;

    @GetMapping("/transactions")
    public ResponseEntity<Object> getAllTransactions() {
        log.info("Inside getAllTransactions");
        List<TransactionDto> transactions = service.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping("/transactions/new")
    public ResponseEntity<Object> createTransaction(@Valid @RequestBody TransactionDto transaction, BindingResult bindingResult) {
        log.info("Inside createTransaction");
        try {
            if(!bindingResult.hasErrors()) {
                Transaction t = service.saveTransaction(transaction);
                return new ResponseEntity<>(t, HttpStatus.CREATED);
            } else {
                List<CustomError> allErrors = util.listAllCustomErrors(bindingResult);
                log.error("Error creating transaction: {}", allErrors);
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch(AccountDoesNotExistException ae) {
            log.error("Error inside createTransaction: {}", ae.getMessage());
            return new ResponseEntity<>(ae.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("Error inside createTransaction: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
