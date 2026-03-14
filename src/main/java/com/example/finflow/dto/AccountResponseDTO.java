package com.example.finflow.dto;

import com.example.finflow.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDTO {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal  balance;
    private String currency;
    private LocalDateTime createdAt;
}
