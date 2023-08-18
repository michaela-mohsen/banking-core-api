package com.banking.springboot.controller;


import com.banking.springboot.auth.UserRepository;
import com.banking.springboot.dto.LoginDto;
import com.banking.springboot.dto.UserDto;
import com.banking.springboot.entity.Employee;
import com.banking.springboot.exceptions.CustomError;
import com.banking.springboot.exceptions.UserAlreadyExistsException;
import com.banking.springboot.repository.EmployeeRepository;
import com.banking.springboot.security.jwt.JwtResponse;
import com.banking.springboot.service.impl.AuthService;
import com.banking.springboot.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private Utility utility;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/sign-in")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {
        try {
            if(!bindingResult.hasErrors()) {
                JwtResponse response = authService.authenticateUser(loginDto);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<CustomError> allErrors = utility.listAllCustomErrors(bindingResult);
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) throws UserAlreadyExistsException {
        try {
            if(!bindingResult.hasErrors()) {
                authService.saveUser(userDto);
                return new ResponseEntity<>("User registered successfully.", HttpStatus.CREATED);
            } else {
                List<CustomError> allErrors = utility.listAllCustomErrors(bindingResult);
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (UserAlreadyExistsException ue) {
            return new ResponseEntity<>(ue.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-all-users")
    public ResponseEntity<Object> deleteAllExistingUsers() {
        try {
            List<Employee> employees = employeeRepository.findAll();
            if(!employees.isEmpty()) {
                for(Employee employee : employees) {
                    if(employee.getUser() != null) {
                        employee.setUser(null);
                    }
                }
            }
            userRepository.deleteAll();
            return new ResponseEntity<>("All users deleted", HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
