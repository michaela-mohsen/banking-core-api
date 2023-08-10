package com.banking.springboot.repository;

import com.banking.springboot.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AccountRepository extends JpaRepository<Account, Integer> {


    List<Account> findByProductTypeOrderById(String type);

    List<Account> findByCustomerId(Integer id);

    Account findAccountById(Integer id);
}
