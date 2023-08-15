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
    public Transaction saveTransaction(Integer otherAccount, TransactionDto t) throws JsonProcessingException, AccountDoesNotExistException {
        ObjectMapper mapper = new ObjectMapper();
        log.info("Inside saveTransferTransaction: {}", mapper.writeValueAsString(t));
        Account source = accountRepository.findAccountById(t.getAccount());
        Transaction transactionSource = new Transaction();
        if(source != null) {
            Double pendingBalanceSource = 0.0;
            if(t.getType().equalsIgnoreCase("DEPOSIT") || t.getType().equalsIgnoreCase("WITHDRAWAL")) {
                pendingBalanceSource = setPendingBalance(t.getAmount(), source.getPendingBalance(), t.getType());
            } else {
                if(otherAccount != null) {
                    pendingBalanceSource = setPendingBalance(t.getAmount(), source.getPendingBalance(), "TRANSFER");
                    Account other = accountRepository.findAccountById(otherAccount);
                    if(other != null) {
                        if(other.getCustomer().getId().equals(source.getCustomer().getId())) {
                            Transaction transactionOther = new Transaction();
                            transactionOther.setAmount(t.getAmount());
                            transactionOther.setFundsAvailableDate(LocalDate.now().plusDays(1));
                            transactionOther.setDate(LocalDateTime.now());
                            transactionSource.setType(t.getType() + " TO ACCOUNT " + otherAccount);
                            transactionOther.setType(t.getType() + " FROM ACCOUNT " + t.getAccount());
                            Double pendingBalanceOther = setPendingBalance(t.getAmount(), other.getPendingBalance(), "DEPOSIT");
                            other.setPendingBalance(pendingBalanceOther);
                            other.setLastActivityDate(LocalDateTime.now());
                            accountRepository.save(other);
                            transactionOther.setAccount(other);
                            repository.save(transactionOther);
                        } else {
                            throw new AccountDoesNotExistException("Customer does not own account with id " + otherAccount);
                        }
                    } else {
                        throw new AccountDoesNotExistException("Account not found. Please select an existing account.");
                    }
                }
            }
            source.setPendingBalance(pendingBalanceSource);
            source.setLastActivityDate(LocalDateTime.now());
            accountRepository.save(source);
            transactionSource.setAmount(t.getAmount());
            transactionSource.setType(t.getType());
            transactionSource.setFundsAvailableDate(LocalDate.now().plusDays(1));
            transactionSource.setDate(LocalDateTime.now());
            transactionSource.setAccount(source);
            repository.save(transactionSource);
        } else {
            log.error("Account not found with id {}", t.getAccount());
            throw new AccountDoesNotExistException("Account does not exist with id " + t.getAccount());
        }

        return transactionSource;
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
            if(newPendingBalance < 0) {
                throw new IllegalArgumentException("Unable to complete transaction: Insufficient funds.");
            }
        }
        return newPendingBalance;
    }
}
