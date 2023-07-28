package com.banking.springboot.service;

import java.util.List;

import com.banking.springboot.dto.AccountDto;
import com.banking.springboot.dto.TransactionDto;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.NoTransactionsException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.banking.springboot.entity.Transaction;

@Component
public interface TransactionService {

	List<TransactionDto> getAllTransactions();

	List<TransactionDto> convertAccountTransactionsToJson(Integer id) throws AccountDoesNotExistException, NoTransactionsException;

	Transaction saveTransaction(TransactionDto transaction);

	Page<Transaction> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
}
