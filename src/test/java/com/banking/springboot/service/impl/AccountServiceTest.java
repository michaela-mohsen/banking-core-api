package com.banking.springboot.service.impl;

import com.banking.springboot.dto.AccountDto;
import com.banking.springboot.entity.*;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.repository.*;
import com.banking.springboot.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Autowired
    private AccountServiceImpl service;

    @MockBean
    private AccountRepository accountRepo;

    @MockBean
    private CustomerRepository customerRepo;

    @MockBean
    private BranchRepository branchRepo;

    @MockBean
    private ProductRepository productRepo;

    @MockBean
    private EmployeeRepository employeeRepo;

    @MockBean
    private Utility utility;

    @Test
    void getAllAccountsTest() {
        Account newAccount = new Account();
        List<Account> accountList = new ArrayList<>();
        accountList.add(newAccount);
        AccountDto accountJson = new AccountDto();
        when(accountRepo.findAll()).thenReturn(accountList);
        when(utility.convertAccountToJson(any())).thenReturn(accountJson);
        Assertions.assertEquals(1, service.getAllAccounts().size());
    }

    @Test
    void getAccountByIdTest() throws AccountDoesNotExistException {
        Account newAccount = new Account();
        newAccount.setId(1);
        AccountDto accountJson = new AccountDto();
        accountJson.setId(1);
        when(accountRepo.findAccountById(1)).thenReturn(newAccount);
        when(utility.convertAccountToJson(newAccount)).thenReturn(accountJson);
        service.getAccountById(1);
        Assertions.assertThrows(AccountDoesNotExistException.class, () -> service.getAccountById(2));
    }

    @Test
    void deleteAccountByIdTest() throws AccountDoesNotExistException {
        Account account = new Account();
        account.setId(1);
        when(accountRepo.findAccountById(1)).thenReturn(account);
        service.deleteAccountById(1);
        Assertions.assertThrows(AccountDoesNotExistException.class, () -> service.deleteAccountById(2));
    }

    @Test
    void saveAccountTest() throws CustomerDoesNotExistException, JsonProcessingException {
        AccountDto dto = new AccountDto();
        dto.setId(1);
        dto.setBirthDate("2000-12-31");
        dto.setLastName("LastName");
        dto.setEmployee(1);
        dto.setBranch("Branch");
        dto.setProduct("Product");
        when(customerRepo.findByBirthDateAndLastName(any(), eq("LastName"))).thenReturn(new Customer());
        when(customerRepo.findByBirthDateAndLastName(any(),eq("UnknownLastName"))).thenReturn(null);
        when(branchRepo.findByName(any())).thenReturn(new Branch());
        when(productRepo.findByName(any())).thenReturn(new Product());
        when(employeeRepo.findEmployeeById(any())).thenReturn(new Employee());
        service.saveAccount(dto);
        dto.setLastName("UnknownLastName");
        Assertions.assertThrows(CustomerDoesNotExistException.class, () -> service.saveAccount(dto));
    }

    @Test
    void toggleAccountStatusTest() throws JsonProcessingException {
        Account account = new Account();
        account.setActive(true);
        AccountDto accountJson = new AccountDto();
        accountJson.setActive("false");
        when(accountRepo.findAccountById(any())).thenReturn(account);
        when(utility.convertAccountToJson(any())).thenReturn(accountJson);
        AccountDto updatedAccount = service.toggleAccountStatus(1, accountJson);
        Assertions.assertNotEquals("true", updatedAccount.getActive());
        service.toggleAccountStatus(1, updatedAccount);
    }

    @Test
    void getAccountsByProductTypeTest() {
        Account account = new Account();
        Product product = new Product();
        product.setType("LOAN");
        account.setProduct(product);
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        when(accountRepo.findByProductTypeOrderById(any())).thenReturn(accounts);
        when(utility.convertAccountToJson(any())).thenReturn(new AccountDto());
        Assertions.assertEquals(1, service.getAccountsByProductType("LOAN").size());
    }

    @Test
    void getAccountsByCustomerId() throws AccountDoesNotExistException, CustomerDoesNotExistException {
        Account account = new Account();
        List<Account> accounts = new ArrayList<>();
        accounts.add(account);
        List<Account> emptyAccounts = new ArrayList<>();
        Customer customer1 = new Customer();
        customer1.setId(1);
        Customer customer2 = new Customer();
        customer2.setId(2);
        when(customerRepo.findCustomerById(1)).thenReturn(customer1);
        when(customerRepo.findCustomerById(2)).thenReturn(customer2);
        when(customerRepo.findCustomerById(3)).thenReturn(null);
        when(accountRepo.findByCustomerId(1)).thenReturn(accounts);
        when(accountRepo.findByCustomerId(2)).thenReturn(emptyAccounts);
        when(utility.convertAccountToJson(any())).thenReturn(new AccountDto());
        service.getAccountsByCustomerId(1);
        Assertions.assertThrows(AccountDoesNotExistException.class, () -> service.getAccountsByCustomerId(2));
        Assertions.assertThrows(CustomerDoesNotExistException.class, () -> service.getAccountsByCustomerId(3));
    }

}
