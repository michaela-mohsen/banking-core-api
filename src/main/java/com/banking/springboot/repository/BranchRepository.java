package com.banking.springboot.repository;

import com.banking.springboot.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    public Branch findByName(String name);

}
