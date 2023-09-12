package com.banking.springboot.service;

import com.banking.springboot.dto.EmployeeDto;
import com.banking.springboot.entity.Employee;
import com.banking.springboot.exceptions.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EmployeeService {
	List<Employee> getAllEmployees();

	Employee getEmployeeById(Integer id);

	EmployeeDto getEmployeeByEmail(String email) throws EmployeeDoesNotExistException;

	Employee saveEmployee(EmployeeDto employee) throws BranchDoesNotExistException, DepartmentDoesNotExistException, EmployeeAlreadyExistsException, InvalidOldPasswordException;

	Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
}
