package com.banking.springboot.repository;

import com.banking.springboot.entity.Account;
import com.banking.springboot.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AccountRepository extends JpaRepository<Account, Integer> {


    List<Account> findByProductTypeOrderById(String type);

    List<Account> findByCustomerId(Integer id);

    @Query("select a from Account a where a.id = ?1")
    Account findAccountById(Integer id);
}
