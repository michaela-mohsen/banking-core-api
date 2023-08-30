package com.banking.springboot.security.services;

import com.banking.springboot.auth.*;
import com.banking.springboot.dto.EmployeeDto;
import com.banking.springboot.dto.LoginDto;
import com.banking.springboot.dto.UserDto;
import com.banking.springboot.entity.RefreshToken;
import com.banking.springboot.exceptions.*;
import com.banking.springboot.repository.DepartmentRepository;
import com.banking.springboot.repository.EmployeeRepository;
import com.banking.springboot.security.jwt.JwtResponse;
import com.banking.springboot.security.jwt.JwtUtils;
import com.banking.springboot.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private EmployeeServiceImpl employeeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService tokenService;

    public JwtResponse authenticateUser(LoginDto loginRequest) throws UserDoesNotExistException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = new ArrayList<>();
        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            String authority = grantedAuthority.getAuthority();
            roles.add(authority);
        }
        RefreshToken refreshToken;
        try {
            refreshToken = tokenService.createRefreshToken(userDetails.getId());
        } catch (UserDoesNotExistException e) {
            throw new UserDoesNotExistException("User not found with id " + userDetails.getId());
        }
        JwtResponse response = new JwtResponse();
        response.setRefreshToken(refreshToken.getToken());
        response.setToken(jwt);
        response.setUsername(userDetails.getUsername());
        response.setEmail(userDetails.getEmail());
        response.setRoles(roles);
        response.setId(userDetails.getId());
        return response;
    }

    public User saveUser(UserDto userDto) throws UserAlreadyExistsException, DepartmentDoesNotExistException, BranchDoesNotExistException, EmployeeAlreadyExistsException {
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
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        Role userRole = roleRepository.findByName("ROLE_USER");
        if(userRole != null) {
            if(userDto.getTitle().equalsIgnoreCase("Administrator")) {
                roles.add(adminRole);
            }
            roles.add(userRole);
        }
        newUser.setRoles(roles);
        userRepository.save(newUser);
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName(userDto.getFirstName());
        employeeDto.setLastName(userDto.getLastName());
        employeeDto.setEmail(userDto.getEmail());
        employeeDto.setTitle(userDto.getTitle());
        employeeDto.setBranch(userDto.getBranch());

        try {
            employeeService.saveEmployee(employeeDto);
        } catch (Exception e) {
            log.error("Error saving employee: {}", e.getMessage());
            throw e;
        }
        return newUser;
    }
}
