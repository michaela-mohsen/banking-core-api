package com.banking.springboot.service.impl;

import com.banking.springboot.auth.*;
import com.banking.springboot.dto.LoginDto;
import com.banking.springboot.dto.UserDto;
import com.banking.springboot.entity.Employee;
import com.banking.springboot.exceptions.UserAlreadyExistsException;
import com.banking.springboot.repository.EmployeeRepository;
import com.banking.springboot.security.jwt.JwtResponse;
import com.banking.springboot.security.jwt.JwtUtils;
import com.banking.springboot.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    public JwtResponse authenticateUser(LoginDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            String authority = grantedAuthority.getAuthority();
            roles.add(authority);
        }
        JwtResponse response = new JwtResponse();
        response.setToken(jwt);
        response.setUsername(userDetails.getUsername());
        response.setEmail(userDetails.getEmail());
        response.setRoles(roles);
        response.setId(userDetails.getId());
        return response;
    }

    public User saveUser(UserDto userDto) throws UserAlreadyExistsException {
        User newUser = new User();
        String encodedPassword = encoder.encode(userDto.getPassword());
        User existingUser = userRepository.findByEmail(userDto.getEmail());
        if(existingUser != null) {
            throw new UserAlreadyExistsException("User already exists with email " + userDto.getEmail());
        }
        newUser.setUsername(userDto.getEmail());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setAvatar(userDto.getAvatar());
        newUser.setCreateDate(LocalDateTime.now());
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER);
        if(userRole != null) {
            roles.add(userRole);
        }
        newUser.setRoles(roles);
        userRepository.save(newUser);
        Employee e = employeeRepository.findEmployeeByEmail(userDto.getEmail());
        if(e != null) {
            e.setUser(newUser);
            employeeRepository.save(e);
        } else {
            Employee newEmployee = new Employee();
            newEmployee.setFirstName(userDto.getFirstName());
            newEmployee.setLastName(userDto.getLastName());
            newEmployee.setEmail(userDto.getEmail());
            newEmployee.setTitle(userDto.getTitle());
            newEmployee.setUser(newUser);
            newEmployee.setStartDate(LocalDate.now());
            employeeRepository.save(newEmployee);
        }
        return newUser;
    }
}
