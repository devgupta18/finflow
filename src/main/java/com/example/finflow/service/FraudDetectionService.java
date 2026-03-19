package com.example.finflow.service;

import com.example.finflow.entity.Transaction;
import com.example.finflow.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FraudDetectionService {
    TransactionRepository transactionRepository;
    public FraudDetectionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    private List<Transaction> getRecentTransactions(Transaction transaction, LocalDateTime time) {
        return transactionRepository.findByAccountAndTimestampAfter(transaction.getAccount(), time);
    }

    private boolean isOddHour(@org.jetbrains.annotations.NotNull Transaction transaction) {
        LocalTime time = transaction.getTimestamp().toLocalTime();
        LocalTime start = LocalTime.MIDNIGHT;
        LocalTime end = LocalTime.of(5, 0);
        return !time.isBefore(start) && time.isBefore(end);
    }

    private boolean isHighFrequency(Transaction transaction) {
        LocalDateTime time = LocalDateTime.now().minusMinutes(10);
        List<Transaction> recent = getRecentTransactions(transaction, time);
        return recent.size() > 5;
    }

    private boolean isCardTesting(Transaction transaction) {
        LocalDateTime time = LocalDateTime.now().minusMinutes(10);
        List<Transaction> recent = getRecentTransactions(transaction, time);
        boolean smallAmount = recent.stream().anyMatch(t -> t.getAmount().compareTo(BigDecimal.TEN) < 0);
        return transaction.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0 && smallAmount;
    }

    @Async("fraudDetectionExecutor")
    public CompletableFuture<Boolean> analyzeTransaction(Transaction transaction) {
        boolean isHighFrequency = isHighFrequency(transaction);
        boolean isOddHour = isOddHour(transaction);
        boolean isCardTesting = isCardTesting(transaction);
        if (isHighFrequency || isOddHour || isCardTesting) {
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.completedFuture(false);
    }
}
