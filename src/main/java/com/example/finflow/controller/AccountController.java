package com.example.finflow.controller;

import com.example.finflow.dto.AccountRequestDTO;
import com.example.finflow.dto.AccountResponseDTO;
import com.example.finflow.dto.AccountUpdateRequestDTO;
import com.example.finflow.dto.TransactionResponseDTO;
import com.example.finflow.entity.Account;
import com.example.finflow.entity.Transaction;
import com.example.finflow.mapper.TransactionMapper;
import com.example.finflow.service.AccountService;
import com.example.finflow.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactionsByAccountId(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getTransactionsByAccountId(id, pageable);
        Page<TransactionResponseDTO> response = transactions.map(TransactionMapper::toTransactionResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Long id, @RequestBody AccountUpdateRequestDTO accountUpdateRequestDTO) {
        Account account = accountService.updateAccount(id, accountUpdateRequestDTO.getType(), accountUpdateRequestDTO.getCurrency());
        AccountResponseDTO accountResponseDTO = getAccountResponseDTO(account);
        return ResponseEntity.status(HttpStatus.OK).body(accountResponseDTO);
    }

}
