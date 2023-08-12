package com.banking.springboot.service.impl;

import com.banking.springboot.dto.TransactionDto;
import com.banking.springboot.entity.Account;
import com.banking.springboot.entity.Transaction;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.repository.AccountRepository;
import com.banking.springboot.repository.TransactionRepository;
import com.banking.springboot.service.TransactionService;
import com.banking.springboot.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private Utility util;

    @Override
    public List<TransactionDto> getAllTransactions() {
        log.info("Inside getAllTransactions");
        List<Transaction> transactions = repository.findAll();
        List<TransactionDto> transactionsJson = new ArrayList<>();
        for (Transaction t : transactions) {
            TransactionDto transactionJson = util.convertTransactionToJson(t);
            transactionsJson.add(transactionJson);
        }
        return transactionsJson;
    }

    @Override
    public Transaction saveTransaction(TransactionDto t) throws JsonProcessingException, AccountDoesNotExistException {
        ObjectMapper mapper = new ObjectMapper();
        log.info("Inside saveTransaction: {}", mapper.writeValueAsString(t));
        Transaction transaction = new Transaction();
        transaction.setAmount(t.getAmount());
        transaction.setFundsAvailableDate(LocalDate.now().plusDays(1));
        transaction.setDate(LocalDateTime.now());
        transaction.setType(t.getType().toUpperCase());

        Account account = accountRepository.findAccountById(t.getAccount());
        if(account != null) {
            Double pendingBalance = setPendingBalance(t.getAmount(), account.getPendingBalance(), t.getType());
            account.setPendingBalance(pendingBalance);
            account.setLastActivityDate(LocalDateTime.now());
            accountRepository.save(account);
        } else {
            log.error("Account not found with id {}", t.getAccount());
            throw new AccountDoesNotExistException("Account does not exist with id " + t.getAccount());
        }
        transaction.setAccount(account);
        repository.save(transaction);
        return transaction;
    }

    @Override
    public Page<Transaction> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.repository.findAll(pageable);
    }

    private Double setPendingBalance(Double amount, Double pendingBalance, String type) {
        double newPendingBalance;
        if (type.equalsIgnoreCase("DEPOSIT")) {
            newPendingBalance = pendingBalance + amount;
        } else {
            newPendingBalance = pendingBalance - amount;
        }
        return newPendingBalance;
    }
}
