package com.example.finflow.mapper;

import com.example.finflow.dto.TransactionResponseDTO;
import com.example.finflow.entity.Transaction;

public class TransactionMapper {
    public static TransactionResponseDTO toResponseDTO(Transaction transaction1) {
        TransactionResponseDTO transactionResponseDTO1 = new TransactionResponseDTO();
        transactionResponseDTO1.setId(transaction1.getId());
        transactionResponseDTO1.setDescription(transaction1.getDescription());
        transactionResponseDTO1.setTransactionType(transaction1.getType());
        transactionResponseDTO1.setTransactionStatus(transaction1.getStatus());
        transactionResponseDTO1.setAmount(transaction1.getAmount());
        transactionResponseDTO1.setTimestamp(transaction1.getTimestamp());
        transactionResponseDTO1.setAccountId(transaction1.getAccount().getId());
        return transactionResponseDTO1;
    }
}
