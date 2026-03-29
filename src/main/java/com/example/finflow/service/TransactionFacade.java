package com.example.finflow.service;

import com.example.finflow.entity.Transaction;
import com.example.finflow.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionFacade {
    private final TransactionService transactionService;
    private static final Logger log = LoggerFactory.getLogger(TransactionFacade.class);

    public TransactionFacade(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public List<Transaction> fallbackTransaction(Long sourceAccountId, Long destinationAccountId , BigDecimal amount, String description, String idempotencyKey, Exception e) {
        log.error("Database is down, transaction cannot be processed: {}", e.getMessage());
        throw new ServiceUnavailableException("Transaction service is currently unavailable");
    }

    @CircuitBreaker(name = "transactionDB", fallbackMethod = "fallbackTransaction")
    public List<Transaction> postTransaction(Long sourceAccountId, Long destinationAccountId , BigDecimal amount, String description, String idempotencyKey) {
        return transactionService.postTransaction(
                sourceAccountId,
                destinationAccountId,
                amount,
                description,
                idempotencyKey
        );
    }
}
