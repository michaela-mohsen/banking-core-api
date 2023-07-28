package com.banking.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.banking.springboot.entity.Transaction;

import java.util.List;

@Component
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByAccountIdOrderByDateDesc(Integer id);
}
