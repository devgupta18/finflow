package com.example.finflow.repository;

import com.example.finflow.entity.Account;
import com.example.finflow.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page< Transaction> findByAccountId(Long accountId, Pageable pageable);
    List<Transaction> findByAccountAndTimestampAfter(Account account, LocalDateTime since);
    List<Transaction> findByAccountIdAndTimestampAfter(Long accountId, LocalDateTime since);
}
