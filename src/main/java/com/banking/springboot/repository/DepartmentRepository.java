package com.banking.springboot.repository;

import com.banking.springboot.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Department findByName(String name);
}
