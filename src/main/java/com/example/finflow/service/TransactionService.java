package com.example.finflow.service;

import com.example.finflow.entity.*;
import com.example.finflow.exception.*;
import com.example.finflow.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final FraudDetectionService fraudDetectionService;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, FraudDetectionService fraudDetectionService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.fraudDetectionService = fraudDetectionService;
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

    @Transactional(noRollbackFor = InsufficientFundsException.class)
    public List<Transaction> postTransaction(Long sourceAccountId, Long destinationAccountId , BigDecimal amount, String description) {
        // We will acquire lock for smaller account no first to ensure no deadlock occurs
        Long firstLockId = sourceAccountId < destinationAccountId ? sourceAccountId : destinationAccountId;
        Long secondLockId = sourceAccountId < destinationAccountId ? destinationAccountId : sourceAccountId;

        // Fetch firstLockId & secondLockId
        Account firstAccount = accountRepository.findByIdWithLock(firstLockId).orElseThrow( () -> new AccountNotFoundException("Account not found") );
        Account secondAccount = accountRepository.findByIdWithLock(secondLockId).orElseThrow( () -> new AccountNotFoundException("Account not found") );

        // Assigning back values to sourceAccount & destinationAccount
        Account sourceAccount = firstAccount.getId().equals(sourceAccountId) ? firstAccount : secondAccount;
        Account destinationAccount = firstAccount.getId().equals(sourceAccountId) ? secondAccount : firstAccount;

        // If balance < 0, log this and don't process transfer
        if(sourceAccount.getBalance().compareTo(amount) < 0){
            transactionRepository.save(buildTransaction(sourceAccount, TransactionType.DEBIT, amount, description,  TransactionStatus.FAILED));
            throw new InsufficientFundsException("Insufficient funds");
        }

        // Update balance
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        // Save updated accounts first
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Log the transactions
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transactionRepository.save(buildTransaction(sourceAccount, TransactionType.DEBIT, amount, description, TransactionStatus.COMPLETED)));
        transactions.add(transactionRepository.save(buildTransaction(destinationAccount, TransactionType.CREDIT, amount, description,  TransactionStatus.COMPLETED)));

        fraudDetectionService.analyzeTransaction(transactions.get(0))
                .thenAccept(isFraudulent -> {
                   if(isFraudulent) {
                       log.warn("Fraud detected for transaction {}", transactions.get(0).getId());
                   }
                });

        return transactions;
    }

    public Page<Transaction> getTransactionsByAccountId(Long accountId, Pageable pageable) {
        return transactionRepository.findByAccountId(accountId, pageable);
    }
}
