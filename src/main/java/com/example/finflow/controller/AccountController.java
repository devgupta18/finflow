package com.example.finflow.controller;

import com.example.finflow.dto.AccountRequestDTO;
import com.example.finflow.dto.AccountResponseDTO;
import com.example.finflow.dto.TransactionResponseDTO;
import com.example.finflow.entity.Account;
import com.example.finflow.entity.Transaction;
import com.example.finflow.mapper.TransactionMapper;
import com.example.finflow.service.AccountService;
import com.example.finflow.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;
    public AccountController(AccountService accountService,   TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    private static AccountResponseDTO getAccountResponseDTO(Account account) {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setId(account.getId());
        accountResponseDTO.setAccountNumber(account.getAccountNumber());
        accountResponseDTO.setAccountType(account.getType());
        accountResponseDTO.setBalance(account.getBalance());
        accountResponseDTO.setCurrency(account.getCurrency());
        accountResponseDTO.setCreatedAt(account.getCreatedAt());
        return accountResponseDTO;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        Account account = accountService.createAccount(accountRequestDTO.getUserId(), accountRequestDTO.getAccountType(), accountRequestDTO.getCurrency());
        AccountResponseDTO accountResponseDTO = getAccountResponseDTO(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        AccountResponseDTO accountResponseDTO =  getAccountResponseDTO(account);
        return ResponseEntity.status(HttpStatus.OK).body(accountResponseDTO);
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAccountId(@PathVariable Long id) {
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(id);
        List<TransactionResponseDTO>  transactionResponseDTO = new ArrayList<>();
        for (Transaction transaction1 : transactions) {
            TransactionResponseDTO transactionResponseDTO1 = TransactionMapper.toResponseDTO(transaction1);
            transactionResponseDTO.add(transactionResponseDTO1);
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponseDTO);
    }

}
