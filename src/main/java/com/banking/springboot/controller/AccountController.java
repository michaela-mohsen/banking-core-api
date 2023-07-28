package com.banking.springboot.controller;

import com.banking.springboot.dto.AccountDto;
import com.banking.springboot.entity.Account;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.service.impl.AccountServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@CrossOrigin
@Slf4j
public class AccountController {

	private AccountServiceImpl accountService;

	public AccountController(AccountServiceImpl accountService) {
		super();
		this.accountService = accountService;
	}


	@GetMapping("/accounts/{id}")
	public ResponseEntity<?> getAccountById(@PathVariable Integer id) {
		try {
			log.debug("Inside getAccountById AccountController");
			AccountDto account = accountService.getAccountById(id);
			return new ResponseEntity<>(account, HttpStatus.OK);
		} catch (AccountDoesNotExistException ae) {
			return new ResponseEntity<>(ae.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// get all accounts
	@GetMapping("/accounts")
	public ResponseEntity<?> listAccounts() {
		try {
			log.debug("Inside listAccounts AccountController");
			List<AccountDto> accounts = accountService.getAllAccounts();
			return new ResponseEntity<>(accounts, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// filter accounts by type
	@GetMapping("/accounts/search")
	public ResponseEntity<?> listAccountsByProductType(@RequestParam("type") String type) {
		try {
			log.debug("Inside listAccountsByProductType AccountController {}", type);
			List<AccountDto> accountsByProduct = accountService.getAccountsByProductType(type);
			return new ResponseEntity<>(accountsByProduct, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/accounts/customer/{id}")
	public ResponseEntity<?> listAccountsByCustomerId(@PathVariable Integer id) {
		try {
			log.debug("Inside getAccountsByCustomerId AccountController {}", id);
			List<AccountDto> accountsByCustomer = accountService.getAccountsByCustomerId(id);
			return new ResponseEntity<>(accountsByCustomer, HttpStatus.OK);
		} catch (CustomerDoesNotExistException ce) {
			return new ResponseEntity<>(ce.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// create a new account object
	@PostMapping("/accounts/new")
	public ResponseEntity<?> createAccount(@RequestBody AccountDto accountDto) {
		try {
			log.debug("Inside createAccount AccountController {}", accountDto);
			Account newAccount = accountService.saveAccount(accountDto);
			return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// activate/deactivate an account object
	@PutMapping("/accounts/status/{id}")
	public ResponseEntity<?> toggleAccount(@PathVariable Integer id, @RequestBody AccountDto data) {
		log.debug("Inside toggleAccount AccountController {}", data);
		AccountDto existingAccount = accountService.toggleAccountStatus(id, data);
		return new ResponseEntity<>(existingAccount, HttpStatus.OK);
	}

}
