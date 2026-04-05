package com.example.finflow.service;

import com.example.finflow.entity.Account;
import com.example.finflow.entity.Transaction;
import com.example.finflow.exception.InsufficientFundsException;
import com.example.finflow.repository.AccountRepository;
import com.example.finflow.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionEventProducer transactionEventProducer;
    @Mock
    private IdempotencyService idempotencyService;
    @Mock
    private Account sourceAccount;
    @Mock
    private Account destinationAccount;

    private void initializingAllMocks(int sourceBalance) {
        Mockito.when(sourceAccount.getId()).thenReturn(1L);
        Mockito.when(sourceAccount.getBalance()).thenReturn(new BigDecimal(sourceBalance));
        Mockito.when(destinationAccount.getId()).thenReturn(2L);
        Mockito.when(accountRepository.findByIdWithLock(1L)).thenReturn(Optional.of(sourceAccount));
        Mockito.when(accountRepository.findByIdWithLock(2L)).thenReturn(Optional.of(destinationAccount));
        Mockito.when(idempotencyService.isIdempotent("asdf")).thenReturn(false);
    }

    @Test
    void postTransaction_success() {
        // Arrange
        initializingAllMocks(1000);
        Mockito.when(destinationAccount.getBalance()).thenReturn(new BigDecimal(2000));
        Mockito.when(transactionRepository.save(Mockito.any())).thenReturn(new Transaction());

        // Act
        List<Transaction> transactions = transactionService.postTransaction(sourceAccount.getId(), destinationAccount.getId(), new BigDecimal("500"), "Testing postTransaction", "asdf");

        // Assert
        Assertions.assertNotNull(transactions);
        Assertions.assertEquals(2, transactions.size());
        Mockito.verify(idempotencyService, Mockito.times(1)).storeKey("asdf");
        Mockito.verify(transactionRepository, Mockito.times(2)).save(Mockito.any());
    }

    @Test
    void postTransaction_insufficientFunds() {
        // Arrange
        initializingAllMocks(100);

        // Assert
        Assertions.assertThrows(InsufficientFundsException.class, () -> {
            transactionService.postTransaction(sourceAccount.getId(), destinationAccount.getId(), new BigDecimal("500"), "Testing postTransaction failure for insufficient funds", "asdf");
        });
    }
}
