package com.example.finflow.controller;

import com.example.finflow.dto.TransactionRequestDTO;
import com.example.finflow.dto.TransactionResponseDTO;
import com.example.finflow.entity.Transaction;
import com.example.finflow.mapper.TransactionMapper;
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
            TransactionResponseDTO transactionResponseDTO1 = TransactionMapper.toResponseDTO(transaction1);
            transactionResponseDTO.add(transactionResponseDTO1);
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponseDTO);
    }


}
