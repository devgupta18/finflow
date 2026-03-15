package com.example.finflow.service;

import com.example.finflow.dto.TransactionRequestDTO;
import com.example.finflow.entity.BatchJob;
import com.example.finflow.entity.BatchJobStatus;
import com.example.finflow.repository.BatchJobRepository;
import com.example.finflow.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BatchJobProcessor {
    TransactionService transactionService;
    BatchJobRepository  batchJobRepository;

    public BatchJobProcessor(TransactionService transactionService,  BatchJobRepository batchJobRepository) {
        this.transactionService = transactionService;
        this.batchJobRepository = batchJobRepository;
    }

    @Async
    public void processBatchJob(Long jobId, List<TransactionRequestDTO> transactions) {
        BatchJob batchJob = batchJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Batch Job Not Found"));
        batchJob.setStatus(BatchJobStatus.PROCESSING);
        batchJobRepository.save(batchJob);
        for (TransactionRequestDTO t : transactions) {
            try {
                transactionService.postTransaction(t.getSourceAccountId(), t.getDestinationAccountId(), t.getAmount(), t.getDescription());
                batchJob.setProcessedTransactions(batchJob.getProcessedTransactions() + 1);
                batchJobRepository.save(batchJob);
            } catch (Exception ex) {
                batchJob.setFailedTransactions(batchJob.getFailedTransactions() + 1);
                batchJobRepository.save(batchJob);
                continue;
            }
        }
        batchJob.setStatus(BatchJobStatus.COMPLETED);
        batchJob.setCompleteAt(LocalDateTime.now());
        batchJobRepository.save(batchJob);
    }
}
