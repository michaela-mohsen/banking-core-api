package com.banking.springboot.service;

import java.util.List;

import com.banking.springboot.dto.AccountDto;
import com.banking.springboot.entity.Account;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.exceptions.NoTransactionsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

@Component
public interface AccountService {
	List<AccountDto> getAllAccounts() throws NoTransactionsException;

	List<AccountDto> getAccountsByProductType(String type);

	List<AccountDto> getAccountsByCustomerId(Integer id) throws CustomerDoesNotExistException, AccountDoesNotExistException, NoTransactionsException;

	AccountDto getAccountById(Integer id) throws AccountDoesNotExistException, NoTransactionsException;

	void deleteAccountById(Integer id);

	Account saveAccount(AccountDto account) throws CustomerDoesNotExistException, JsonProcessingException;

	AccountDto toggleAccountStatus(Integer id, AccountDto account) throws JsonProcessingException;

}
