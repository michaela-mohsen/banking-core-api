package com.banking.springboot.controller;

import com.banking.springboot.dto.EmployeeDto;
import com.banking.springboot.entity.Employee;
import com.banking.springboot.exceptions.*;
import com.banking.springboot.service.impl.EmployeeServiceImpl;
import com.banking.springboot.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/user")
@CrossOrigin("http://localhost:3000")
@RestController
@Slf4j
public class EmployeeController {
    @Autowired
    private EmployeeServiceImpl service;

    @Autowired
    private Utility utility;

    @GetMapping("/employees/search")
//    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> findEmployeeByEmail(@RequestParam String email) {
        log.info("Inside findEmployeeByEmail: {}", email);
        try {
            EmployeeDto employee = service.getEmployeeByEmail(email);
            return new ResponseEntity<>(employee, HttpStatus.OK);
        } catch (EmployeeDoesNotExistException ee) {
            return new ResponseEntity<>(ee.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> findAllEmployees() {
        log.info("Inside findAllEmployees");
        try {
            List<Employee> employees = service.getAllEmployees();
            return new ResponseEntity<>(employees, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/employees/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> createNewEmployee(@Valid @RequestBody EmployeeDto employeeDto, BindingResult bindingResult) {
        try {
            if(!bindingResult.hasErrors()) {
                Employee employee = service.saveEmployee(employeeDto);
                return new ResponseEntity<>(employee, HttpStatus.CREATED);
            } else {
                List<CustomError> allErrors = utility.listAllCustomErrors(bindingResult);
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (EmployeeAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/employees/update/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Object> updateEmployee(@PathVariable Integer id, @Valid @RequestBody EmployeeDto employeeDto, BindingResult bindingResult) {
        try {
            if(!bindingResult.hasErrors()) {
                Employee employee = service.updateEmployee(employeeDto, id);
                return new ResponseEntity<>(employee, HttpStatus.CREATED);
            } else {
                List<CustomError> allErrors = utility.listAllCustomErrors(bindingResult);
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (InvalidOldPasswordException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (BranchDoesNotExistException | DepartmentDoesNotExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
