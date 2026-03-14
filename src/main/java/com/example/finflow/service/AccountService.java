package com.example.finflow.service;

import com.example.finflow.entity.Account;
import com.example.finflow.entity.AccountType;
import com.example.finflow.entity.User;
import com.example.finflow.repository.AccountRepository;
import com.example.finflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public Account createAccount(Long userId, AccountType type, String currency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Account account = new Account();
        account.setType(type);
        account.setCurrency(currency);
        account.setCreatedAt(LocalDateTime.now());
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber("ACC-" + System.currentTimeMillis());
        account.setUser(user);
        return accountRepository.save(account);
    }
}
