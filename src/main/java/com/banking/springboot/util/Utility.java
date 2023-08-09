package com.banking.springboot.util;

import com.banking.springboot.dto.*;
import com.banking.springboot.entity.*;
import com.banking.springboot.exceptions.CustomError;
import com.banking.springboot.exceptions.NoTransactionsException;
import com.banking.springboot.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Utility {

    @Autowired
    private TransactionRepository transactionRepository;

    public AccountDto convertAccountToJson(Account a) {
        log.debug("Converting account to json {}", a);
        List<TransactionDto> transactionsToJSON;
        try {
            transactionsToJSON = convertAccountTransactionsToJson(a.getId());
        } catch (Exception e) {
            transactionsToJSON = new ArrayList<>();
        }
        AccountDto dto = new AccountDto();
        dto.setId(a.getId());
        Double updatedBalance = updateAvailableBalance(a.getLastActivityDate().toLocalDate(), a.getPendingBalance(), a.getAvailableBalance());
        dto.setAvailableBalance(updatedBalance);
        dto.setPendingBalance(a.getPendingBalance());
        dto.setActive(a.getActive().toString());
        dto.setBirthDate(a.getCustomer().getBirthDate().toString());
        dto.setLastName(a.getCustomer().getLastName());
        dto.setBranch(a.getBranch().getName());
        dto.setEmployee(a.getEmployee().getId());
        dto.setProduct(a.getProduct().getName());
        dto.setOpenDate(formatLocalDate(a.getOpenDate()));
        dto.setLastActivityDate(formatLocalDateTime(a.getLastActivityDate()));
        dto.setTransactions(transactionsToJSON);
        log.debug("Done converting account to JSON");
        return dto;
    }

    public Double updateAvailableBalance(LocalDate lastActivityDate, Double pendingBalance, Double availableBalance) {
        log.debug("Inside updateAvailableBalance AccountServiceImpl");
        Double updatedBalance;
        LocalDate now = LocalDate.now();
        if(now.isAfter(lastActivityDate)) {
            updatedBalance = availableBalance;
            log.debug("Balance not changed");
        } else {
            updatedBalance = pendingBalance;
            log.info("Balance updated");
        }
        return updatedBalance;
    }

    public TransactionDto convertTransactionToJson(Transaction t) {
        TransactionDto transactionJson = new TransactionDto();
        transactionJson.setAmount(t.getAmount());
        transactionJson.setFundsAvailableDate(formatLocalDate(t.getFundsAvailableDate()));
        transactionJson.setDate(formatLocalDate(t.getDate().toLocalDate()));
        transactionJson.setTime(formatLocalTime(t.getDate().toLocalTime()));
        transactionJson.setType(t.getType());
        transactionJson.setAccount(t.getAccount().getId());
        return transactionJson;
    }

    public List<TransactionDto> convertAccountTransactionsToJson(Integer id) throws NoTransactionsException {
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByDateDesc(id);
        if (!transactions.isEmpty()) {
            List<TransactionDto> transactionsJson = new ArrayList<>();
            for (Transaction t : transactions) {
                TransactionDto transactionJson = convertTransactionToJson(t);
                transactionsJson.add(transactionJson);
            }
            return transactionsJson;
        } else {
            throw new NoTransactionsException("Account with id " + id + " does not have any transactions.");
        }
    }

    public CustomerDto convertCustomerToJson(Customer c) {
        CustomerDto customerJson = new CustomerDto();
        customerJson.setId(c.getId());
        customerJson.setFirstName(c.getFirstName());
        customerJson.setLastName(c.getLastName());
        customerJson.setBirthDate(formatLocalDate(c.getBirthDate()));
        customerJson.setAddress(c.getAddress());
        customerJson.setCity(c.getCity());
        customerJson.setState(c.getState());
        customerJson.setZipCode(c.getZipCode());
        return customerJson;
    }

    public ProductDto convertProductToJson(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setType(p.getType());
        return dto;
    }

    public BranchDto convertBranchToJson(Branch b) {
        BranchDto dto = new BranchDto();
        dto.setName(b.getName());
        dto.setAddress(b.getAddress());
        dto.setCity(b.getCity());
        dto.setState(b.getState());
        dto.setZipCode(b.getZipCode());

        return dto;
    }

    public String formatLocalDate(LocalDate localDate) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return localDate.format(dateFormatter);
    }

    public String formatLocalTime(LocalTime localTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        return localTime.format(timeFormatter);
    }

    public String formatLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        return localDateTime.format(dateTimeFormatter);
    }

    public List<CustomError> listAllCustomErrors(BindingResult bindingResult) {
        List<CustomError> allErrors = new ArrayList<>();
        for(FieldError error : bindingResult.getFieldErrors()) {
            CustomError customError = new CustomError();
            customError.setField(error.getField());
            customError.setMessage(error.getDefaultMessage());
            allErrors.add(customError);
        }
        return allErrors;
    }


}
