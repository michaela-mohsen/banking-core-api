package com.banking.springboot.service;

import com.banking.springboot.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EmployeeService {
	List<Employee> getAllEmployees();

	Employee getEmployeeById(Integer id);

	Employee getEmployeeByEmail(String email);

	Employee saveEmployee(Employee employee);

	Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection);
}
