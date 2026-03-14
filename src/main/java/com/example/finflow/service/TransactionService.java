package com.example.finflow.service;

import com.example.finflow.entity.*;
import com.example.finflow.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    private Transaction buildTransaction(Account account, TransactionType type, BigDecimal amount, String description, TransactionStatus status) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setDescription(description);
        transaction.setStatus(status);
        transaction.setTimestamp(LocalDateTime.now());
        return transaction;
    }

    @Transactional(dontRollbackOn = RuntimeException.class)
    public void postTransaction(Long sourceAccountId, Long destinationAccountId , BigDecimal amount, String description) {
        // Fetch source and destination accounts
        Account sourceAccount = accountRepository.findById(sourceAccountId).orElseThrow( () -> new RuntimeException("Account not found") );
        Account destinationAccount = accountRepository.findById(destinationAccountId).orElseThrow( () -> new RuntimeException("Account not found") );

        // If balance < 0, log this and don't process transfer
        if(sourceAccount.getBalance().compareTo(amount) < 0){
            transactionRepository.save(buildTransaction(sourceAccount, TransactionType.DEBIT, amount, description,  TransactionStatus.FAILED));
            throw new RuntimeException("Insufficient funds");
        }

        // Update balance
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        // Save updated accounts first
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Log the transactions
        transactionRepository.save(buildTransaction(sourceAccount, TransactionType.DEBIT, amount, description, TransactionStatus.COMPLETED));
        transactionRepository.save(buildTransaction(destinationAccount, TransactionType.CREDIT, amount, description,  TransactionStatus.COMPLETED));
    }
}
