package com.banking.springboot.service.impl;

import com.banking.springboot.dto.AccountDto;
import com.banking.springboot.entity.*;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.repository.*;
import com.banking.springboot.service.AccountService;
import com.banking.springboot.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private Utility util;

	@Autowired
	private TransactionServiceImpl transactionService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public List<AccountDto> getAllAccounts() {
		log.info("Inside getAllAccounts");
		List<Account> accounts = accountRepository.findAll();
		log.info(accounts.size() + " accounts found");
		List<AccountDto> accountsToJSON = new ArrayList<>();
		for(Account a : accounts) {
			AccountDto dto = util.convertAccountToJson(a);
			accountsToJSON.add(dto);
		}
		return accountsToJSON;
	}

	@Override
	public AccountDto getAccountById(Integer id) throws AccountDoesNotExistException {
		Account a = accountRepository.findAccountById(id);
		if(a != null) {
			log.debug("Account found with id {}", id);
			return util.convertAccountToJson(a);
		} else {
			log.error("Account not found with id {}", id);
			throw new AccountDoesNotExistException("Account does not exist with id" + id);
		}
	}

	@Override
	public void deleteAccountById(Integer id) throws AccountDoesNotExistException {
		log.info("Inside deleteAccountById {}", id);
		Account deleteAccount = accountRepository.findAccountById(id);
		if(deleteAccount != null) {
			accountRepository.delete(deleteAccount);
			log.info("Account deleted.");
		} else {
			log.error("Account not found with id {}",id);
			throw new AccountDoesNotExistException("Account does not exist with id " + id);
		}
	}

	@Override
	public Account saveAccount(AccountDto accountDto) throws CustomerDoesNotExistException {
		Account newAccount = new Account();
		Customer customer = customerRepository.findByBirthDateAndLastName(LocalDate.parse(accountDto.getBirthDate()), accountDto.getLastName());
		if(customer != null) {
			log.debug("Customer found with birth date " + customer.getBirthDate() + " and last name " + customer.getLastName());
			newAccount.setCustomer(customer);
		} else {
			log.error("Customer not found with birth date " + accountDto.getBirthDate() + " and last name " + accountDto.getLastName());
			throw new CustomerDoesNotExistException("Customer does not exist.");
		}
		Branch branch = branchRepository.findByName(accountDto.getBranch());
		Product product = productRepository.findByName(accountDto.getProduct());
		Employee employee = employeeRepository.findEmployeeById(accountDto.getEmployee());
		newAccount.setAvailableBalance(accountDto.getAvailableBalance());
		newAccount.setLastActivityDate(LocalDateTime.now());
		newAccount.setOpenDate(LocalDate.now());
		newAccount.setPendingBalance(accountDto.getPendingBalance());
		newAccount.setActive(true);
		newAccount.setBranch(branch);
		newAccount.setProduct(product);
		newAccount.setEmployee(employee);
		log.debug("New account saved");
		return accountRepository.save(newAccount);
	}

	@Override
	public AccountDto toggleAccountStatus(Integer id, AccountDto dto) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		log.info("Inside toggleAccountStatus {}", mapper.writeValueAsString(dto));
		Account a = accountRepository.findAccountById(id);
		log.debug("Account found with id " + id);
		boolean newAccountStatus = Boolean.parseBoolean(dto.getActive());
		if(!a.getActive().equals(newAccountStatus)) {
			a.setActive(newAccountStatus);
			a.setLastActivityDate(LocalDateTime.now());
			accountRepository.save(a);
			log.info("Account active: " + newAccountStatus);
		} else {
			log.info("Account status unchanged.");
		}
		dto = util.convertAccountToJson(a);
		return dto;
	}

	@Override
	public List<AccountDto> getAccountsByProductType(String type) {
		log.info("Inside getAccountsByProductType: {}", type);
		List<Account> accounts = accountRepository.findByProductTypeOrderById(type);
		log.info(accounts.size() + " accounts found with product type " + type);
		List<AccountDto> accountsToJSON = new ArrayList<>();
		for(Account a : accounts) {
			AccountDto dto = util.convertAccountToJson(a);
			accountsToJSON.add(dto);
		}
		return accountsToJSON;
	}

	@Override
	public List<AccountDto> getAccountsByCustomerId(Integer id) throws CustomerDoesNotExistException, AccountDoesNotExistException {
		log.info("Inside getAccountsByCustomerId: {}", id);
		Customer customer = customerRepository.findCustomerById(id);
		List<AccountDto> accountsToJSON = new ArrayList<>();
		if(customer != null) {
			List<Account> accounts = accountRepository.findByCustomerId(id);
			log.info(accounts.size() + " accounts found for customer with id " + id);
			if(!accounts.isEmpty()) {
				for(Account account : accounts) {
					AccountDto dto = util.convertAccountToJson(account);
					accountsToJSON.add(dto);
				}
			} else {
				log.error("Customer with id " + id + " does not have any accounts.");
				throw new AccountDoesNotExistException("Customer " + id + " does not have any accounts.");
			}
		} else {
			log.error("No customer found with id {}", id);
			throw new CustomerDoesNotExistException("Customer not found.");
		}
		return accountsToJSON;
	}

}
