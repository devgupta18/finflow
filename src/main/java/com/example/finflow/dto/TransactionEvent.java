package com.example.finflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent implements Serializable {
    private Long transactionId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private Long accountId;
}
