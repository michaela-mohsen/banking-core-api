package com.banking.springboot.controller;

import com.banking.springboot.dto.CustomerDto;
import com.banking.springboot.entity.Customer;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.service.impl.CustomerServiceImpl;
import com.banking.springboot.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
class CustomerControllerTest {
    @InjectMocks
    private CustomerController controller;

    @Mock
    private CustomerServiceImpl service;

    @MockBean
    private Utility utility;

    @BeforeEach
    public void init() {
        openMocks(this);
    }

    @Test
    void listCustomersTest() {
        CustomerDto customerJson = new CustomerDto();
        Customer customer = new Customer();
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        when(service.getAllCustomersPageable(any())).thenReturn(new PageImpl<>(customers));
        when(service.getCustomersByLastNameContaining(any(),any())).thenReturn(new PageImpl<>(customers));
        when(utility.convertCustomerToJson(any())).thenReturn(customerJson);
        Assertions.assertEquals(HttpStatus.OK,controller.listCustomers(null,0,10).getStatusCode());
        Assertions.assertEquals(HttpStatus.OK, controller.listCustomers("", 0, 10).getStatusCode());
    }

    @Test
    void getCustomerByIdTest() throws CustomerDoesNotExistException {
        CustomerDto customer = new CustomerDto();
        when(service.getCustomerById(1)).thenReturn(customer);
        when(service.getCustomerById(2)).thenThrow(CustomerDoesNotExistException.class);
        when(service.getCustomerById(3)).thenThrow(RuntimeException.class);
        Assertions.assertEquals(HttpStatus.OK, controller.getCustomerById(1).getStatusCode());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.getCustomerById(2).getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getCustomerById(3).getStatusCode());
    }

    @Test
    void getCustomerByLastNameAndBirthDateTest() throws CustomerDoesNotExistException {
        CustomerDto c = new CustomerDto();
        when(service.getCustomerByBirthDateAndLastName(any(), eq("LastName"))).thenReturn(c);
        when(service.getCustomerByBirthDateAndLastName(any(), eq("UnknownLastName"))).thenThrow(CustomerDoesNotExistException.class);
        when(service.getCustomerByBirthDateAndLastName(any(), eq("SomethingElse"))).thenThrow(RuntimeException.class);
        Assertions.assertEquals(HttpStatus.OK, controller.getCustomerByLastNameAndBirthDate("LastName", LocalDate.now().toString()).getStatusCode());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.getCustomerByLastNameAndBirthDate("UnknownLastName", LocalDate.now().toString()).getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getCustomerByLastNameAndBirthDate("SomethingElse", LocalDate.now().toString()).getStatusCode());
    }

    @Test
    void createCustomerTest() {
        CustomerDto c = new CustomerDto();
        CustomerDto cWithErrors = new CustomerDto();
        CustomerDto cWithException = new CustomerDto();
        c.setFirstName("First");
        c.setLastName("Last");
        c.setAddress("Address");
        c.setCity("City");
        c.setState("PA");
        c.setZipCode("12345");
        c.setBirthDate(LocalDate.now().toString());
        BindingResult brNoErrors = new BeanPropertyBindingResult(c, "c");
        BindingResult brWithErrors = new BeanPropertyBindingResult(cWithErrors, "cWithErrors");
        brWithErrors.addError(new FieldError("cWithErrors", "firstName", "No First Name"));
        when(service.saveCustomer(c)).thenReturn(new Customer());
        when(service.saveCustomer(cWithException)).thenThrow(RuntimeException.class);
        when(utility.listAllCustomErrors(brWithErrors)).thenReturn(new ArrayList<>());
        Assertions.assertEquals(HttpStatus.CREATED, controller.createCustomer(c, brNoErrors).getStatusCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.createCustomer(cWithErrors, brWithErrors).getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.createCustomer(cWithException, brNoErrors).getStatusCode());
    }

    @Test
    void updateCustomerTest() throws CustomerDoesNotExistException, JsonProcessingException {
        CustomerDto c = new CustomerDto();
        CustomerDto cWithErrors = new CustomerDto();
        CustomerDto cNotExists = new CustomerDto();
        CustomerDto cWithException = new CustomerDto();
        c.setId(1);
        c.setFirstName("First");
        c.setLastName("Last");
        c.setAddress("Address");
        c.setCity("City");
        c.setState("PA");
        c.setZipCode("12345");
        c.setBirthDate(LocalDate.now().toString());
        BindingResult brNoErrors = new BeanPropertyBindingResult(c, "c");
        BindingResult brWithErrors = new BeanPropertyBindingResult(cWithErrors, "cWithErrors");
        brWithErrors.addError(new FieldError("cWithErrors", "firstName", "No First Name"));
        when(service.updateCustomer(1, c)).thenReturn(new Customer());
        when(service.updateCustomer(2, cNotExists)).thenThrow(CustomerDoesNotExistException.class);
        when(service.updateCustomer(3, cWithException)).thenThrow(JsonProcessingException.class);
        when(utility.listAllCustomErrors(brWithErrors)).thenReturn(new ArrayList<>());
        Assertions.assertEquals(HttpStatus.OK, controller.updateCustomer(1, c, brNoErrors).getStatusCode());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.updateCustomer(2, cNotExists, brNoErrors).getStatusCode());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.updateCustomer(3, cWithException, brNoErrors).getStatusCode());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.updateCustomer(4, cWithErrors, brWithErrors).getStatusCode());
    }
}
