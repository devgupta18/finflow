package com.example.finflow.controller;

import com.example.finflow.dto.TransactionRequestDTO;
import com.example.finflow.dto.TransactionResponseDTO;
import com.example.finflow.entity.Transaction;
import com.example.finflow.service.AccountService;
import com.example.finflow.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<List<TransactionResponseDTO>> createTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO) {
        List<Transaction> transaction = transactionService.postTransaction(transactionRequestDTO.getSourceAccountId(), transactionRequestDTO.getDestinationAccountId(), transactionRequestDTO.getAmount(), transactionRequestDTO.getDescription());
        List<TransactionResponseDTO>  transactionResponseDTO = new ArrayList<>();
        for (Transaction transaction1 : transaction) {
            TransactionResponseDTO transactionResponseDTO1 = new TransactionResponseDTO();
            transactionResponseDTO1.setId(transaction1.getId());
            transactionResponseDTO1.setDescription(transaction1.getDescription());
            transactionResponseDTO1.setTransactionType(transaction1.getType());
            transactionResponseDTO1.setTransactionStatus(transaction1.getStatus());
            transactionResponseDTO1.setAmount(transaction1.getAmount());
            transactionResponseDTO1.setTimestamp(transaction1.getTimestamp());
            transactionResponseDTO1.setAccountId(transaction1.getAccount().getId());
            transactionResponseDTO.add(transactionResponseDTO1);
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponseDTO);
    }
}
