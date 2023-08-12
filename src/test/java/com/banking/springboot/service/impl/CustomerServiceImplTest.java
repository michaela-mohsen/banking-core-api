package com.banking.springboot.service.impl;

import com.banking.springboot.dto.CustomerDto;
import com.banking.springboot.entity.Customer;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.repository.CustomerRepository;
import com.banking.springboot.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomerServiceImplTest {
    @Autowired
    private CustomerServiceImpl service;
    @MockBean
    private CustomerRepository repository;
    @MockBean
    private Utility utility;

    @Test
    void getAllCustomers() {
        Customer customer = new Customer();
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        when(repository.findAll()).thenReturn(customers);
        Assertions.assertEquals(1, service.getAllCustomers().size());
    }

    @Test
    void getAllCustomersPageableTest() {
        Customer customer = new Customer();
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        Pageable pageable = Pageable.unpaged();
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(customers));
        Assertions.assertEquals(1, service.getAllCustomersPageable(pageable).getTotalElements());
    }

    @Test
    void getCustomerByIdTest() throws CustomerDoesNotExistException {
        Customer customer = new Customer();
        when(repository.findCustomerById(1)).thenReturn(customer);
        when(utility.convertCustomerToJson(any())).thenReturn(new CustomerDto());
        Assertions.assertNotNull(service.getCustomerById(1));
        Assertions.assertThrows(CustomerDoesNotExistException.class, () -> service.getCustomerById(2));
    }

    @Test
    void getCustomerByBirthDateAndLastNameTest() throws CustomerDoesNotExistException {
        when(repository.findByBirthDateAndLastName(any(), eq("LastName"))).thenReturn(new Customer());
        when(repository.findByBirthDateAndLastName(any(), eq("UnknownLastName"))).thenReturn(null);
        when(utility.convertCustomerToJson(any())).thenReturn(new CustomerDto());
        Assertions.assertNotNull(service.getCustomerByBirthDateAndLastName(LocalDate.now(), "LastName"));
        Assertions.assertThrows(CustomerDoesNotExistException.class, () -> service.getCustomerByBirthDateAndLastName(LocalDate.now(), "UnknownLastName"));
    }

    @Test
    void saveCustomerTest() {
        CustomerDto customer = testCustomerDto();
        when(repository.save(any())).thenReturn(new Customer());
        Assertions.assertNotNull(service.saveCustomer(customer));
    }

    @Test
    void deleteCustomerById() throws CustomerDoesNotExistException {
        when(repository.findCustomerById(1)).thenReturn(new Customer());
        when(repository.findCustomerById(2)).thenReturn(null);
        service.deleteCustomerById(1);
        Assertions.assertThrows(CustomerDoesNotExistException.class, () -> service.deleteCustomerById(2));
    }

    @Test
    void updateCustomerTest() throws CustomerDoesNotExistException, JsonProcessingException {
        CustomerDto customerDto = testCustomerDto();
        CustomerDto otherCustomerDto = testCustomerDto();
        otherCustomerDto.setLastName("UnknownLastName");
        when(repository.findCustomerById(1)).thenReturn(testCustomer());
        when(repository.findCustomerById(2)).thenReturn(null);
        when(repository.save(any())).thenReturn(testCustomer());
        Assertions.assertNotNull(service.updateCustomer(1, customerDto));
        Assertions.assertThrows(CustomerDoesNotExistException.class, () -> service.updateCustomer(2, otherCustomerDto));
    }

    @Test
    void getCustomersByLastNameContainingTest() {
        Customer customer = new Customer();
        List<Customer> customers = new ArrayList<>();
        customers.add(customer);
        Pageable pageable = Pageable.unpaged();
        when(repository.findByLastNameContaining(any(), any())).thenReturn(new PageImpl<>(customers));
        Assertions.assertEquals(1, service.getCustomersByLastNameContaining("LastNa", pageable).getTotalElements());
    }

    private CustomerDto testCustomerDto() {
        CustomerDto dto = new CustomerDto();
        dto.setBirthDate(LocalDate.now().toString());
        dto.setFirstName("FirstName");
        dto.setLastName("LastName");
        dto.setAddress("Address");
        dto.setCity("City");
        dto.setState("PA");
        dto.setZipCode("12345");
        return dto;
    }

    private Customer testCustomer() {
        Customer c = new Customer();
        c.setBirthDate(LocalDate.now());
        c.setFirstName("FirstName");
        c.setLastName("LastName");
        c.setAddress("Address");
        c.setCity("City");
        c.setState("PA");
        c.setZipCode("12345");
        return c;
    }
}
