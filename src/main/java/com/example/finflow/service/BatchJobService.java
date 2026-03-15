package com.example.finflow.service;

import com.example.finflow.dto.TransactionRequestDTO;
import com.example.finflow.entity.BatchJob;
import com.example.finflow.entity.BatchJobStatus;
import com.example.finflow.entity.Transaction;
import com.example.finflow.repository.BatchJobRepository;
import com.example.finflow.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BatchJobService {
    BatchJobRepository batchJobRepository;
    BatchJobProcessor batchJobProcessor;
    public BatchJobService(BatchJobRepository batchJobRepository,  BatchJobProcessor batchJobProcessor) {
        this.batchJobRepository = batchJobRepository;
        this.batchJobProcessor = batchJobProcessor;
    }

    public Long createBatchJob(List<TransactionRequestDTO> transactions) {
        BatchJob batchJob = new BatchJob();
        batchJob.setStatus(BatchJobStatus.PENDING);
        batchJob.setTotalTransactions(transactions.size());
        batchJob.setProcessedTransactions(0);
        batchJob.setFailedTransactions(0);
        batchJob.setCreatedAt(LocalDateTime.now());
        batchJobRepository.save(batchJob);
        Long id  = batchJob.getId();
        batchJobProcessor.processBatchJob(id, transactions);
        return batchJob.getId();
    }

    public BatchJob getBatchJob(Long id) {
        BatchJob batchJob = batchJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        return batchJob;
    }
}
