package com.example.finflow.service;

import com.example.finflow.entity.*;
import com.example.finflow.exception.*;
import com.example.finflow.repository.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalApplicationListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return transactions;
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
}
