package com.example.finflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "transactions")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
