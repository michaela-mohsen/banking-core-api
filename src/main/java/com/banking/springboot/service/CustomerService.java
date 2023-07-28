package com.banking.springboot.service;

import com.banking.springboot.dto.CustomerDto;
import com.banking.springboot.entity.Customer;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public interface CustomerService {

	List<Customer> getAllCustomers();

	Page<Customer> getAllCustomersPageable(Pageable pageable);

	CustomerDto getCustomerById(Integer id) throws CustomerDoesNotExistException;

	CustomerDto getCustomerByBirthDateAndLastName(LocalDate birthDate, String lastName) throws CustomerDoesNotExistException;

	Customer saveCustomer(CustomerDto customer);

	void deleteCustomerById(Integer id) throws CustomerDoesNotExistException;

	Customer updateCustomer(CustomerDto customer) throws CustomerDoesNotExistException;

	Page<Customer> getCustomersByLastNameContaining(String keyword, Pageable pageable) throws CustomerDoesNotExistException;
}
