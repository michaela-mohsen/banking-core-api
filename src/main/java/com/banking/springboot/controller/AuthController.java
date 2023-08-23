package com.banking.springboot.controller;


import com.banking.springboot.auth.UserRepository;
import com.banking.springboot.dto.LoginDto;
import com.banking.springboot.dto.UserDto;
import com.banking.springboot.entity.Employee;
import com.banking.springboot.exceptions.CustomError;
import com.banking.springboot.exceptions.TokenRefreshException;
import com.banking.springboot.exceptions.UserAlreadyExistsException;
import com.banking.springboot.repository.EmployeeRepository;
import com.banking.springboot.security.jwt.JwtResponse;
import com.banking.springboot.security.jwt.TokenRefreshRequest;
import com.banking.springboot.security.jwt.TokenRefreshResponse;
import com.banking.springboot.security.services.AuthService;
import com.banking.springboot.security.services.RefreshTokenService;
import com.banking.springboot.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private Utility utility;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/log-in")
    public ResponseEntity<Object> loginPage() {
        return new ResponseEntity<>("Here's the login page.", HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginDto loginDto, BindingResult bindingResult) {
        log.info("Begin log in");
        try {
            if(!bindingResult.hasErrors()) {
                JwtResponse response = authService.authenticateUser(loginDto);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                List<CustomError> allErrors = utility.listAllCustomErrors(bindingResult);
                return new ResponseEntity<>(allErrors, HttpStatus.BAD_REQUEST);
            }
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Incorrect email and/or password. Please try again.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
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

    @PostMapping("/refresh-token")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody TokenRefreshRequest refreshRequest) {
        try {
            TokenRefreshResponse response = refreshTokenService.refreshToken(refreshRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TokenRefreshException te) {
            return new ResponseEntity<>(te.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sign-out")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Object> logoutUser() {
        log.info("Begin log out");
        try {
            refreshTokenService.deleteByUserId();
            return new ResponseEntity<>("User successfully logged out.", HttpStatus.OK);
        } catch (IllegalArgumentException ie) {
            return new ResponseEntity<>("User not found.", HttpStatus.BAD_REQUEST);
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
