package com.banking.springboot.service.impl;

import com.banking.springboot.dto.AccountDto;
import com.banking.springboot.dto.TransactionDto;
import com.banking.springboot.entity.*;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.exceptions.NoTransactionsException;
import com.banking.springboot.repository.*;
import com.banking.springboot.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {


	private final AccountRepository accountRepository;

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

	public AccountServiceImpl(AccountRepository accountRepository) {
		super();
		this.accountRepository = accountRepository;
	}

	@Override
	public List<AccountDto> getAllAccounts() {
		log.info("Inside getAllAccounts");
		List<Account> accounts = accountRepository.findAll();
		log.info(accounts.size() + " accounts found");
		List<AccountDto> accountsToJSON = new ArrayList<>();
		for(Account a : accounts) {
			AccountDto dto = accountToJson(a);
			accountsToJSON.add(dto);
		}
		return accountsToJSON;
	}

	@Override
	public AccountDto getAccountById(Integer id) throws AccountDoesNotExistException {
		log.info("Inside getAccountById: {}", id);
		Account a = accountRepository.findAccountById(id);
		if(a != null) {
			log.info("Account found with id {}", id);
			return accountToJson(a);
		} else {
			throw new AccountDoesNotExistException("Account does not exist with id" + id);
		}
	}

	@Override
	public void deleteAccountById(Integer id) {
		accountRepository.deleteById(id);
	}

	@Override
	public Account saveAccount(AccountDto accountDto) throws CustomerDoesNotExistException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		log.info("Inside saveAccount {}", mapper.writeValueAsString(accountDto));
		Account newAccount = new Account();
		try {
			Customer customer = customerRepository.findByBirthDateAndLastName(LocalDate.parse(accountDto.getBirthDate()), accountDto.getLastName());
			log.debug("Customer found with birth date " + customer.getBirthDate() + " and last name " + customer.getLastName());
			newAccount.setCustomer(customer);
		} catch (Exception e) {
			throw new CustomerDoesNotExistException("Customer with birth date " + accountDto.getBirthDate() + " and last name " + accountDto.getLastName() + " does not exist");
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
			log.info("Account status updated to " + newAccountStatus);
		}
		dto = accountToJson(a);
		return dto;
	}

	@Override
	public List<AccountDto> getAccountsByProductType(String type) {
		log.info("Inside getAccountsByProductType: {}", type);
		List<Account> accounts = accountRepository.findByProductTypeOrderById(type);
		log.info(accounts.size() + " accounts found with product type " + type);
		List<AccountDto> accountsToJSON = new ArrayList<>();
		for(Account a : accounts) {
			AccountDto dto = accountToJson(a);
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
			log.info(accounts.size() + " found for customer with id " + id);
			if(!accounts.isEmpty()) {
				for(Account account : accounts) {
					AccountDto dto = accountToJson(account);
					accountsToJSON.add(dto);
				}
			} else {
				throw new AccountDoesNotExistException("Customer with id " + id + " does not have any accounts.");
			}
		} else {
			throw new CustomerDoesNotExistException("Customer with id " + id + " does not exist.");
		}
		return accountsToJSON;
	}

	private Double updateAvailableBalance(LocalDate lastActivityDate, Double pendingBalance, Double availableBalance) {
		log.info("Inside updateAvailableBalance AccountServiceImpl");
		Double updatedBalance;
		LocalDate now = LocalDate.now();
		if(now.isAfter(lastActivityDate)) {
			updatedBalance = pendingBalance;
			log.info("Balance updated");
		} else {
			updatedBalance = availableBalance;
			log.info("Balance not changed");
		}
		return updatedBalance;
	}

	private AccountDto accountToJson(Account a) {
		log.debug("Converting account to json {}", a);
		List<TransactionDto> transactionsToJSON;
		try {
			transactionsToJSON = transactionService.convertAccountTransactionsToJson(a.getId());
		} catch (Exception e) {
			transactionsToJSON = new ArrayList<>();
		}
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
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
		dto.setOpenDate(a.getOpenDate().format(dateFormatter));
		dto.setLastActivityDate(a.getLastActivityDate().format(dateTimeFormatter));
		dto.setTransactions(transactionsToJSON);
		log.debug("Done converting account to JSON");
		return dto;
	}

}
