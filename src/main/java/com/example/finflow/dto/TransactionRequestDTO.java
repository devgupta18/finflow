package com.example.finflow.dto;

import com.example.finflow.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {
    private String description;
    private BigDecimal amount;
    private Long sourceAccountId;
    private Long destinationAccountId;
}
