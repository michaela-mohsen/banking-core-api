package com.banking.springboot.service.impl;

import java.time.LocalDate;
import java.util.List;

import com.banking.springboot.auth.User;
import com.banking.springboot.auth.UserRepository;
import com.banking.springboot.dto.EmployeeDto;
import com.banking.springboot.entity.Branch;
import com.banking.springboot.entity.Department;
import com.banking.springboot.entity.Employee;
import com.banking.springboot.exceptions.BranchDoesNotExistException;
import com.banking.springboot.exceptions.DepartmentDoesNotExistException;
import com.banking.springboot.exceptions.EmployeeAlreadyExistsException;
import com.banking.springboot.repository.BranchRepository;
import com.banking.springboot.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.banking.springboot.repository.EmployeeRepository;
import com.banking.springboot.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	@Override
	public Employee saveEmployee(EmployeeDto employee) throws BranchDoesNotExistException, DepartmentDoesNotExistException, EmployeeAlreadyExistsException {
		Employee e = new Employee();
		Employee existingEmployee = employeeRepository.findEmployeeByEmail(employee.getEmail());
		Branch b = branchRepository.findByName(employee.getBranch());
		Department d = departmentRepository.findByName(employee.getDepartment());
		User u = userRepository.findByEmail(employee.getEmail());
		if(existingEmployee != null) {
			throw new EmployeeAlreadyExistsException("Employee already exists with email " + existingEmployee.getEmail());
		}
		if(b != null) {
			e.setBranch(b);
		} else {
			throw new BranchDoesNotExistException("Branch does not exist. Please select another branch.");
		}
		if(d != null) {
			e.setDepartment(d);
		} else {
			throw new DepartmentDoesNotExistException("Department does not exist. Please select another department.");
		}
		if(u != null) {
			e.setUser(u);
		} else {
			throw new UsernameNotFoundException("Employee has not created a user account with email " + employee.getEmail() + ". Create another user account or use another email.");
		}
		e.setFirstName(employee.getFirstName());
		e.setLastName(employee.getLastName());
		e.setEmail(employee.getEmail());
		e.setStartDate(LocalDate.now());
		e.setTitle(employee.getTitle());
		return employeeRepository.save(e);
	}

	@Override
	public Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.employeeRepository.findAll(pageable);
	}

	public Employee getEmployeeByEmail(String email) {
		return employeeRepository.findEmployeeByEmail(email);
	}

	@Override
	public Employee getEmployeeById(Integer id) {
		return employeeRepository.findEmployeeById(id);
	}

}
