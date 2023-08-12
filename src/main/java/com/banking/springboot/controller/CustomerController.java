package com.banking.springboot.controller;

import com.banking.springboot.dto.CustomerDto;
import com.banking.springboot.entity.Customer;
import com.banking.springboot.exceptions.CustomError;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.service.impl.CustomerServiceImpl;
import com.banking.springboot.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin("http://localhost:3000")
@Slf4j
public class CustomerController {

    private CustomerServiceImpl customerService;

    @Autowired
    private Utility util;

    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public ResponseEntity<Object> listCustomers(@RequestParam(required = false) String lastName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        log.info("Inside listCustomers" );
        Pageable paging = PageRequest.of(page, size);
        Page<Customer> pageCustomers;
        if(lastName == null) {
            pageCustomers = customerService.getAllCustomersPageable(paging);
        } else {
            pageCustomers = customerService.getCustomersByLastNameContaining(lastName, paging);
        }

        List<Customer> customersAsList = pageCustomers.getContent();
        List<CustomerDto> jsonCustomers = new ArrayList<>();
        for (Customer customer : customersAsList) {
            CustomerDto customerJson = util.convertCustomerToJson(customer);
            jsonCustomers.add(customerJson);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("customers", jsonCustomers);
        response.put("currentPage", pageCustomers.getNumber());
        response.put("totalItems",pageCustomers.getTotalElements());
        response.put("totalPages", pageCustomers.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Object> getCustomerById(@PathVariable Integer id) {
        log.info("Inside getCustomerById: {}", id);
        try {
            CustomerDto customer = customerService.getCustomerById(id);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (CustomerDoesNotExistException ce) {
            log.error("Error inside getCustomerById: {}", ce.getMessage());
            return new ResponseEntity<>(ce.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error inside getCustomerById: {}",e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers/search")
    public ResponseEntity<Object> getCustomerByLastNameAndBirthDate(@RequestParam String lastName, @RequestParam String birthDate) {
        log.info("Inside getCustomerByLastNameAndBirthDate: {} {}", lastName, birthDate);
        try {
            CustomerDto customer = customerService.getCustomerByBirthDateAndLastName(LocalDate.parse(birthDate), lastName);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (CustomerDoesNotExistException ce) {
            log.error("Error inside getCustomerByLastNameAndBirthDate: {}", ce.getMessage());
            return new ResponseEntity<>(ce.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error inside getCustomerByLastNameAndBirthDate: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/customers/new")
    public ResponseEntity<Object> createCustomer(@Valid @RequestBody CustomerDto customerDto, BindingResult bindingResult) {
        log.info("Inside createCustomer");
        try {
            if(!bindingResult.hasErrors()) {
                Customer newCustomer = customerService.saveCustomer(customerDto);
                return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
            } else {
                List<CustomError> allErrors = util.listAllCustomErrors(bindingResult);
                log.error("Error creating customer: {}", allErrors);
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error inside createCustomer: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/customers/update/{id}")
    public ResponseEntity<Object> updateCustomer(@PathVariable Integer id, @Valid @RequestBody CustomerDto customerDto, BindingResult bindingResult) {
        log.info("Inside updateCustomer");
        try {
            if(!bindingResult.hasErrors()) {
                Customer updatedCustomer = customerService.updateCustomer(id, customerDto);
                return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
            } else {
                List<CustomError> allErrors = util.listAllCustomErrors(bindingResult);
                log.error("Error creating customer: {}", allErrors);
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (CustomerDoesNotExistException ce) {
            log.error("Error inside updateCustomer: {}", ce.getMessage());
            return new ResponseEntity<>(ce.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error inside updateCustomer: {}",e.getMessage() );
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/customers/delete/{id}")
    public ResponseEntity<Object> deleteCustomer(@PathVariable Integer id) {
        try {
            customerService.deleteCustomerById(id);
            return new ResponseEntity<>("Customer deleted successfully", HttpStatus.OK);
        } catch (CustomerDoesNotExistException ce) {
            return new ResponseEntity<>(ce.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
