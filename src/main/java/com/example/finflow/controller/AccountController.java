package com.example.finflow.controller;

import com.example.finflow.dto.AccountRequestDTO;
import com.example.finflow.dto.AccountResponseDTO;
import com.example.finflow.entity.Account;
import com.example.finflow.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        Account account = accountService.createAccount(accountRequestDTO.getUserId(), accountRequestDTO.getAccountType(), accountRequestDTO.getCurrency());
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setId(account.getId());
        accountResponseDTO.setAccountNumber(account.getAccountNumber());
        accountResponseDTO.setAccountType(account.getType());
        accountResponseDTO.setBalance(account.getBalance());
        accountResponseDTO.setCurrency(account.getCurrency());
        accountResponseDTO.setCreatedAt(account.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDTO);
    }
}
