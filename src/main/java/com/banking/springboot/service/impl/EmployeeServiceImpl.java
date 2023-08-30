package com.banking.springboot.service.impl;

import com.banking.springboot.auth.Role;
import com.banking.springboot.auth.RoleRepository;
import com.banking.springboot.auth.User;
import com.banking.springboot.auth.UserRepository;
import com.banking.springboot.dto.EmployeeDto;
import com.banking.springboot.entity.Branch;
import com.banking.springboot.entity.Department;
import com.banking.springboot.entity.Employee;
import com.banking.springboot.exceptions.BranchDoesNotExistException;
import com.banking.springboot.exceptions.DepartmentDoesNotExistException;
import com.banking.springboot.exceptions.EmployeeAlreadyExistsException;
import com.banking.springboot.exceptions.EmployeeDoesNotExistException;
import com.banking.springboot.repository.BranchRepository;
import com.banking.springboot.repository.DepartmentRepository;
import com.banking.springboot.repository.EmployeeRepository;
import com.banking.springboot.service.EmployeeService;
import com.banking.springboot.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private Utility utility;

	@Override
	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	@Override
	public Employee saveEmployee(EmployeeDto employee) throws EmployeeAlreadyExistsException, DepartmentDoesNotExistException, BranchDoesNotExistException {
		Employee existingEmployee = employeeRepository.findByEmail(employee.getEmail());
		if(existingEmployee == null) {
			try {
				Employee newEmployee = setEmployeeInformation(employee, new Employee());
				return employeeRepository.save(newEmployee);
			} catch(Exception e) {
				log.error("Error saving employee: {}", e.getMessage());
				throw e;
			}
		} else {
			throw new EmployeeAlreadyExistsException("Employee already exists with email " + employee.getEmail());
		}
	}

	public Employee updateEmployee(EmployeeDto employee, Integer id) throws BranchDoesNotExistException, DepartmentDoesNotExistException, EmployeeDoesNotExistException {
		Employee existingEmployee = employeeRepository.findEmployeeById(id);
		if(existingEmployee == null) {
			throw new EmployeeDoesNotExistException("Employee not found with id " + id);
		} else {
			try {
				Employee updatedEmployee = setEmployeeInformation(employee, existingEmployee);
				return employeeRepository.save(updatedEmployee);
			} catch (Exception e) {
				log.error("Error updating employee: {}", e.getMessage());
				throw e;
			}
		}
	}

	@Override
	public Page<Employee> findPaginated(int pageNo, int pageSize, String sortField, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
				: Sort.by(sortField).descending();

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		return this.employeeRepository.findAll(pageable);
	}

	public EmployeeDto getEmployeeByEmail(String email) throws EmployeeDoesNotExistException {
		Employee employee = employeeRepository.findByEmail(email);
		if(employee != null) {
			return utility.convertEmployeeToJson(employee);
		} else {
			throw new EmployeeDoesNotExistException("Employee does not exist with email " + email);
		}
	}

	@Override
	public Employee getEmployeeById(Integer id) {
		return employeeRepository.findEmployeeById(id);
	}

	private Employee setEmployeeInformation(EmployeeDto employeeJson, Employee employeeEntity) throws BranchDoesNotExistException, DepartmentDoesNotExistException {
		Branch b = branchRepository.findByName(employeeJson.getBranch());
		if(b != null) {
			employeeEntity.setBranch(b);
		} else {
			throw new BranchDoesNotExistException("Branch does not exist. Please select another branch.");
		}

		Department d = utility.setDepartmentByTitle(employeeJson.getTitle());
		if(d != null) {
			employeeEntity.setDepartment(d);
		} else {
			throw new DepartmentDoesNotExistException("Department does not exist. Please select another department.");
		}
		employeeEntity.setFirstName(employeeJson.getFirstName());
		employeeEntity.setLastName(employeeJson.getLastName());
		employeeEntity.setEmail(employeeJson.getEmail());
		employeeEntity.setTitle(employeeJson.getTitle());
		if(employeeEntity.getStartDate() == null) {
			employeeEntity.setStartDate(LocalDate.now());
		}
		if(employeeEntity.getUser() != null) {
			User u = employeeEntity.getUser();
			User existingUser = userRepository.findByEmail(u.getEmail());
			if(existingUser != null) {
				Role adminRole = roleRepository.findByName("ROLE_ADMIN");
				if(adminRole != null) {
					if(employeeEntity.getTitle().equalsIgnoreCase("Administrator") && !existingUser.getRoles().contains(adminRole)) {
						existingUser.getRoles().add(adminRole);
					} else if (!employeeEntity.getTitle().equalsIgnoreCase("Administrator") && existingUser.getRoles().contains(adminRole)) {
						existingUser.getRoles().remove(adminRole);
					}
				}
				existingUser.setUsername(employeeJson.getEmail());
				existingUser.setEmail(employeeJson.getEmail());
				existingUser.setPassword(employeeJson.getPassword());
				userRepository.save(existingUser);
				employeeEntity.setUser(existingUser);
			}
		} else {
			User user = userRepository.findByEmail(employeeJson.getEmail());
			employeeEntity.setUser(user);
		}
		return employeeEntity;
	}
}
