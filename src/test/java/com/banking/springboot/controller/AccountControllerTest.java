package com.banking.springboot.controller;

import com.banking.springboot.dto.AccountDto;
import com.banking.springboot.entity.Account;
import com.banking.springboot.exceptions.AccountDoesNotExistException;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.service.impl.AccountServiceImpl;
import com.banking.springboot.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @InjectMocks
    private AccountController controller;

    @Mock
    private AccountServiceImpl service;

    @MockBean
    private Utility utility;

    @BeforeEach
    public void init() {
        openMocks(this);
    }

    @Test
    void getAccountByIdTest() throws AccountDoesNotExistException {
        AccountDto existingAccount = new AccountDto();
        when(service.getAccountById(1)).thenReturn(existingAccount);
        when(service.getAccountById(2)).thenThrow(AccountDoesNotExistException.class);
        when(service.getAccountById(3)).thenThrow(RuntimeException.class);
        Assertions.assertEquals(HttpStatus.OK, controller.getAccountById(1).getStatusCode());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.getAccountById(2).getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getAccountById(3).getStatusCode());
    }

    @Test
    void listAccountsTest() {
        when(service.getAllAccounts()).thenReturn(new ArrayList<>());
        Assertions.assertEquals(HttpStatus.OK, controller.listAccounts().getStatusCode());
    }

    @Test
    void listAccountsByProductTypeTest() {
        when(service.getAccountsByProductType("ACCOUNT")).thenReturn(new ArrayList<>());
        when(service.getAccountsByProductType("SOMETHING ELSE")).thenThrow(RuntimeException.class);
        Assertions.assertEquals(HttpStatus.OK, controller.listAccountsByProductType("ACCOUNT").getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,controller.listAccountsByProductType("SOMETHING ELSE").getStatusCode());
    }

    @Test
    void listAccountsByCustomerId() throws AccountDoesNotExistException, CustomerDoesNotExistException {
        when(service.getAccountsByCustomerId(1)).thenReturn(new ArrayList<>());
        when(service.getAccountsByCustomerId(2)).thenThrow(CustomerDoesNotExistException.class);
        when(service.getAccountsByCustomerId(3)).thenThrow(RuntimeException.class);
        Assertions.assertEquals(HttpStatus.OK, controller.listAccountsByCustomerId(1).getStatusCode());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.listAccountsByCustomerId(2).getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.listAccountsByCustomerId(3).getStatusCode());
    }

    @Test
    void createAccountTest() throws CustomerDoesNotExistException, JsonProcessingException {
        AccountDto accountWithErrors = new AccountDto();
        AccountDto accountWithException = new AccountDto();
        AccountDto account = new AccountDto();
        account.setAvailableBalance(10.00);
        account.setPendingBalance(10.00);
        account.setBirthDate("08-05-2000");
        account.setLastName("LastName");
        account.setBranch("Branch");
        account.setEmployee(1);
        account.setProduct("Product");
        BindingResult brNoErrors = new BeanPropertyBindingResult(account, "account");
        BindingResult brWithErrors = new BeanPropertyBindingResult(accountWithErrors, "accountWithErrors");
        brWithErrors.addError(new FieldError("accountWithErrors", "branch", "No Branch"));
        when(service.saveAccount(account)).thenReturn(new Account());
        when(service.saveAccount(accountWithException)).thenThrow(CustomerDoesNotExistException.class);
        when(utility.listAllCustomErrors(brWithErrors)).thenReturn(new ArrayList<>());
        Assertions.assertEquals(HttpStatus.CREATED, controller.createAccount(account, brNoErrors).getStatusCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.createAccount(accountWithErrors, brWithErrors).getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.createAccount(accountWithException, brNoErrors).getStatusCode());
    }

    @Test
    void toggleAccountTest() throws JsonProcessingException {
        AccountDto account = new AccountDto();
        AccountDto accountBadRequest = new AccountDto();
        AccountDto accountException = new AccountDto();
        when(service.toggleAccountStatus(1, account)).thenReturn(new AccountDto());
        when(service.toggleAccountStatus(2, accountBadRequest)).thenThrow(JsonProcessingException.class);
        when(service.toggleAccountStatus(3, accountException)).thenThrow(RuntimeException.class);
        Assertions.assertEquals(HttpStatus.OK, controller.toggleAccount(1, account).getStatusCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.toggleAccount(2, accountBadRequest).getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.toggleAccount(3, accountException).getStatusCode());
    }

}
