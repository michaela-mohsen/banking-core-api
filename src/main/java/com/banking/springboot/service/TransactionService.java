package com.banking.springboot.service;

import java.util.List;

import com.banking.springboot.dto.TransactionDto;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.banking.springboot.entity.Transaction;

@Component
public interface TransactionService {

	List<TransactionDto> getAllTransactions();

	Transaction saveTransaction(TransactionDto transaction) throws JsonProcessingException, AccountDoesNotExistException;

	Page<Transaction> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
}
