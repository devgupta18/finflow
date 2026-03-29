package com.example.finflow.controller;

import com.example.finflow.dto.TransactionRequestDTO;
import com.example.finflow.dto.TransactionResponseDTO;
import com.example.finflow.entity.Transaction;
import com.example.finflow.mapper.TransactionMapper;
import com.example.finflow.service.TransactionFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionFacade transactionFacade;
    public TransactionController(TransactionFacade transactionFacade) {
        this.transactionFacade = transactionFacade;
    }

    @PostMapping
    public ResponseEntity<List<TransactionResponseDTO>> createTransaction(@RequestBody TransactionRequestDTO transactionRequestDTO, @RequestHeader("Idempotency-Key") String idempotencyKey) {
        List<Transaction> transaction = transactionFacade.postTransaction(transactionRequestDTO.getSourceAccountId(), transactionRequestDTO.getDestinationAccountId(), transactionRequestDTO.getAmount(), transactionRequestDTO.getDescription(), idempotencyKey);
        List<TransactionResponseDTO> transactionResponseDTO = transaction.stream()
                .map(TransactionMapper::toTransactionResponseDTO)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponseDTO);
    }


}
