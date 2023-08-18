package com.banking.springboot.service;

import com.banking.springboot.dto.EmployeeDto;
import com.banking.springboot.entity.Employee;
import com.banking.springboot.exceptions.BranchDoesNotExistException;
import com.banking.springboot.exceptions.DepartmentDoesNotExistException;
import com.banking.springboot.exceptions.EmployeeAlreadyExistsException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EmployeeService {
	List<Employee> getAllEmployees();

	Employee getEmployeeById(Integer id);

	Employee getEmployeeByEmail(String email);

	Employee saveEmployee(EmployeeDto employee) throws BranchDoesNotExistException, DepartmentDoesNotExistException, EmployeeAlreadyExistsException;

	Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
}
