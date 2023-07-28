package com.banking.springboot.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.banking.springboot.dto.AccountDto;
import com.banking.springboot.dto.TransactionDto;
import com.banking.springboot.entity.Account;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.NoTransactionsException;
import com.banking.springboot.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.banking.springboot.entity.Transaction;
import com.banking.springboot.repository.TransactionRepository;
import com.banking.springboot.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private final TransactionRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactions = repository.findAll();
        List<TransactionDto> transactionsJson = new ArrayList<>();
        for (Transaction t : transactions) {
            TransactionDto transactionJson = new TransactionDto();
            transactionJson.setAmount(t.getAmount());
            transactionJson.setFundsAvailableDate(t.getFundsAvailableDate().toString());
            transactionJson.setDate(t.getDate().toLocalDate().toString());
            transactionJson.setTime(t.getDate().toLocalTime().toString());
            transactionJson.setType(t.getType());
            transactionJson.setAccount(t.getAccount().getId());
            transactionsJson.add(transactionJson);
        }
        return transactionsJson;
    }

    @Override
    public List<TransactionDto> convertAccountTransactionsToJson(Integer id) throws NoTransactionsException {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        List<Transaction> transactions = repository.findByAccountIdOrderByDateDesc(id);
        if (!transactions.isEmpty()) {
            List<TransactionDto> transactionsJson = new ArrayList<>();
            for (Transaction t : transactions) {
                TransactionDto transactionJson = new TransactionDto();
                transactionJson.setAmount(t.getAmount());
                transactionJson.setFundsAvailableDate(t.getFundsAvailableDate().format(dateFormatter));
                transactionJson.setDate(t.getDate().toLocalDate().format(dateFormatter));
                transactionJson.setTime(t.getDate().toLocalTime().format(timeFormatter));
                transactionJson.setType(t.getType());
                transactionJson.setAccount(t.getAccount().getId());
                transactionsJson.add(transactionJson);
            }
            return transactionsJson;
        } else {
            throw new NoTransactionsException("Account with id " + id + " does not have any transactions.");
        }
    }

    @Override
    public Transaction saveTransaction(TransactionDto t) {
        Transaction transaction = new Transaction();
        transaction.setAmount(t.getAmount());
        transaction.setFundsAvailableDate(LocalDate.now().plusDays(1));
        transaction.setDate(LocalDateTime.now());
        transaction.setType(t.getType().toUpperCase());

        Account account = accountRepository.findAccountById(t.getAccount());
        Double pendingBalance = setPendingBalance(t.getAmount(), account.getPendingBalance(), t.getType());
        account.setPendingBalance(pendingBalance);
        account.setLastActivityDate(LocalDateTime.now());
        accountRepository.save(account);
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
        Double newPendingBalance = 0.0;
        if (type.equalsIgnoreCase("DEPOSIT")) {
            newPendingBalance = pendingBalance + amount;
        } else {
            newPendingBalance = pendingBalance - amount;
        }
        return newPendingBalance;
    }
}
