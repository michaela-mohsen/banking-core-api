package com.banking.springboot.controller;

import com.banking.springboot.dto.TransactionDto;
import com.banking.springboot.entity.Transaction;
import com.banking.springboot.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin
public class TransactionController {

    @Autowired
    private TransactionServiceImpl service;

    @GetMapping("/transactions")
    public ResponseEntity<Object> getAllTransactions() {
        List<TransactionDto> transactions = service.getAllTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    @PostMapping("/transactions/new")
    public ResponseEntity<Object> createTransaction(@Valid @RequestBody TransactionDto transaction, BindingResult bindingResult) {
        try {
            if(!bindingResult.hasErrors()) {
                Transaction t = service.saveTransaction(transaction);
                return new ResponseEntity<>(t, HttpStatus.CREATED);
            }else {
                Map<String, String> allErrors = new HashMap<>();
                for(FieldError error : bindingResult.getFieldErrors()) {
                    allErrors.put(error.getField(), error.getDefaultMessage());
                }
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
