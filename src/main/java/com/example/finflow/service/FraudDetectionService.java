package com.example.finflow.service;

import com.example.finflow.dto.TransactionEvent;
import com.example.finflow.entity.Transaction;
import com.example.finflow.repository.TransactionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FraudDetectionService {
    private final TransactionRepository transactionRepository;
    private static final Logger log = LoggerFactory.getLogger(FraudDetectionService.class);

    public FraudDetectionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    private List<Transaction> getRecentTransactions(TransactionEvent event, LocalDateTime time) {
        return transactionRepository.findByAccountIdAndTimestampAfter(event.getAccountId(), time);
    }

    private boolean isOddHour(TransactionEvent event) {
        LocalTime time = event.getTimestamp().toLocalTime();
        LocalTime start = LocalTime.MIDNIGHT;
        LocalTime end = LocalTime.of(5, 0);
        return !time.isBefore(start) && time.isBefore(end);
    }

    private boolean isHighFrequency(TransactionEvent event) {
        LocalDateTime time = LocalDateTime.now().minusMinutes(10);
        List<Transaction> recent = getRecentTransactions(event, time);
        return recent.size() > 5;
    }

    private boolean isCardTesting(TransactionEvent event) {
        LocalDateTime time = LocalDateTime.now().minusMinutes(10);
        List<Transaction> recent = getRecentTransactions(event, time);
        boolean smallAmount = recent.stream().anyMatch(t -> t.getAmount().compareTo(BigDecimal.TEN) < 0);
        return event.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0 && smallAmount;
    }

    public CompletableFuture<Boolean> fallbackAnalyze(TransactionEvent event, Exception e) {
        log.warn("Fraud detection service is down: {}", e.getMessage());
        return CompletableFuture.completedFuture(false);
    }

    @Async("fraudDetectionExecutor")
    @CircuitBreaker(name = "fraudDetection", fallbackMethod = "fallbackAnalyze")
    public CompletableFuture<Boolean> analyzeTransaction(TransactionEvent event) {
        boolean isHighFrequency = isHighFrequency(event);
        boolean isOddHour = isOddHour(event);
        boolean isCardTesting = isCardTesting(event);
        if (isHighFrequency || isOddHour || isCardTesting) {
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.completedFuture(false);
    }
}
