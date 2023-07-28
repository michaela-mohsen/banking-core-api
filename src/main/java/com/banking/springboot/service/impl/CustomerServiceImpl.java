package com.banking.springboot.service.impl;

import com.banking.springboot.dto.CustomerDto;
import com.banking.springboot.entity.Customer;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.repository.CustomerRepository;
import com.banking.springboot.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository repository;

	@Override
	public List<Customer> getAllCustomers() {
		return repository.findAll();
	}

	@Override
	public Page<Customer> getAllCustomersPageable(Pageable pageable) {
		return repository.findAll(pageable);
	}

	@Override
	public CustomerDto getCustomerById(Integer id) throws CustomerDoesNotExistException {
		try {
			Customer customer = repository.findById(id).get();
			CustomerDto customerJson = convertCustomerToJson(customer);
			return customerJson;
		} catch (Exception e) {
			throw new CustomerDoesNotExistException("Customer not found with id " + id, e);
		}
	}

	@Override
	public CustomerDto getCustomerByBirthDateAndLastName(LocalDate birthDate, String lastName) throws CustomerDoesNotExistException {
		try {
			Customer c = repository.findByBirthDateAndLastName(birthDate, lastName);
			CustomerDto customerJson = convertCustomerToJson(c);
			return customerJson;
		} catch (Exception e) {
			throw new CustomerDoesNotExistException("Customer not found with birth date " + birthDate + " and last name " + lastName, e);
		}
	}

	@Override
	public Customer saveCustomer(CustomerDto customerDto) {
		Customer newCustomer = new Customer();
		newCustomer.setBirthDate(LocalDate.parse(customerDto.getBirthDate()));
		newCustomer.setFirstName(customerDto.getFirstName());
		newCustomer.setLastName(customerDto.getLastName());
		newCustomer.setAddress(customerDto.getAddress());
		newCustomer.setCity(customerDto.getCity());
		if(listAllStates().contains(customerDto.getState())) {
			newCustomer.setState(customerDto.getState());
		}
		newCustomer.setZipCode(customerDto.getZipCode());
		return repository.save(newCustomer);
	}

	@Override
	public void deleteCustomerById(Integer id) throws CustomerDoesNotExistException {
		try {
			repository.deleteById(id);
		} catch (Exception e) {
			throw new CustomerDoesNotExistException("Customer not found with id " + id, e);
		}
	}

	@Override
	public Customer updateCustomer(CustomerDto customer) throws CustomerDoesNotExistException {
		LocalDate birthDate = LocalDate.parse(customer.getBirthDate());
		Customer existingCustomer = repository.findByBirthDateAndLastName(birthDate, customer.getLastName());
		if(existingCustomer != null) {
			existingCustomer.setFirstName(customer.getFirstName());
			existingCustomer.setLastName(customer.getLastName());
			existingCustomer.setBirthDate(birthDate);
			existingCustomer.setAddress(customer.getAddress());
			existingCustomer.setCity(customer.getCity());
			existingCustomer.setState(customer.getState());
			existingCustomer.setZipCode(customer.getZipCode());
			return repository.save(existingCustomer);
		} else {
			throw new CustomerDoesNotExistException("Customer does not exist with birth date " + birthDate + " and last name " + customer.getLastName());
		}
	}

	@Override
	public Page<Customer> getCustomersByLastNameContaining(String keyword, Pageable pageable) {
		Page<Customer> customers = repository.findByLastNameContaining(keyword, pageable);
		return customers;
	}

	public CustomerDto convertCustomerToJson(Customer c) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		CustomerDto customerJson = new CustomerDto();
		customerJson.setId(c.getId());
		customerJson.setFirstName(c.getFirstName());
		customerJson.setLastName(c.getLastName());
		customerJson.setBirthDate(c.getBirthDate().format(dateFormatter));
		customerJson.setAddress(c.getAddress());
		customerJson.setCity(c.getCity());
		customerJson.setState(c.getState());
		customerJson.setZipCode(c.getZipCode());
		return customerJson;
	}

	private List<String> listAllStates() {
		List<String> states = new ArrayList<>();
		states.add("AL");
		states.add("AK");
		states.add("AZ");
		states.add("AR");
		states.add("CA");
		states.add("CO");
		states.add("CT");
		states.add("DE");
		states.add("FL");
		states.add("GA");
		states.add("HI");
		states.add("ID");
		states.add("IL");
		states.add("IN");
		states.add("IA");
		states.add("KS");
		states.add("KY");
		states.add("LA");
		states.add("ME");
		states.add("MD");
		states.add("MA");
		states.add("MI");
		states.add("MN");
		states.add("MS");
		states.add("MO");
		states.add("MT");
		states.add("NE");
		states.add("NV");
		states.add("NH");
		states.add("NJ");
		states.add("NM");
		states.add("NY");
		states.add("NC");
		states.add("ND");
		states.add("OH");
		states.add("OK");
		states.add("OR");
		states.add("PA");
		states.add("RI");
		states.add("SC");
		states.add("SD");
		states.add("TN");
		states.add("TX");
		states.add("UT");
		states.add("VT");
		states.add("VA");
		states.add("WA");
		states.add("WV");
		states.add("WI");
		states.add("WY");
		return states;
	}

}
