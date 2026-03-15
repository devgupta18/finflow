package com.example.finflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "batchjob")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BatchJobStatus status;
    private Integer totalTransactions;
    private Integer processedTransactions;
    private LocalDateTime createdAt;
    private LocalDateTime completeAt;
}
