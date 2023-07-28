package com.banking.springboot.controller;

import com.banking.springboot.dto.CustomerDto;
import com.banking.springboot.entity.Customer;
import com.banking.springboot.exceptions.CustomerDoesNotExistException;
import com.banking.springboot.service.impl.CustomerServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin
public class CustomerController {

    private CustomerServiceImpl customerService;

    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public ResponseEntity<?> listCustomers(@RequestParam(required = false) String lastName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
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
            CustomerDto customerJson = customerService.convertCustomerToJson(customer);
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
    public ResponseEntity<?> getCustomerById(@PathVariable Integer id) {
        try {
            CustomerDto customer = customerService.getCustomerById(id);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (CustomerDoesNotExistException ce) {
            return new ResponseEntity<>(ce.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers/search")
    public ResponseEntity<?> getCustomerByLastNameAndBirthDate(@RequestParam String lastName, @RequestParam String birthDate) {
        try {
            CustomerDto customer = customerService.getCustomerByBirthDateAndLastName(LocalDate.parse(birthDate), lastName);
            return new ResponseEntity<>(customer, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/customers/new")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerDto customerDto, BindingResult bindingResult) {
        try {
            if(!bindingResult.hasErrors()) {
                Customer newCustomer = customerService.saveCustomer(customerDto);
                return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
            } else {
                Map<String, String> allErrors = new HashMap<>();
                for(FieldError error : bindingResult.getFieldErrors()) {
                    allErrors.put(error.getField(), error.getDefaultMessage());
                }
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/customers/update/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Integer id, @Valid @RequestBody CustomerDto customerDto, BindingResult bindingResult) {
        try {
            if(!bindingResult.hasErrors()) {
                Customer updatedCustomer = customerService.updateCustomer(customerDto);
                return new ResponseEntity<>(updatedCustomer, HttpStatus.OK);
            } else {
                Map<String, String> allErrors = new HashMap<>();
                for(FieldError error : bindingResult.getFieldErrors()) {
                    allErrors.put(error.getField(), error.getDefaultMessage());
                }
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/customers/delete/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer id) {
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
