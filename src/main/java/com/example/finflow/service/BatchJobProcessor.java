package com.example.finflow.service;

import com.example.finflow.dto.TransactionRequestDTO;
import com.example.finflow.entity.BatchJob;
import com.example.finflow.entity.BatchJobStatus;
import com.example.finflow.repository.BatchJobRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BatchJobProcessor {
    private final TransactionService transactionService;
    private final BatchJobRepository  batchJobRepository;
    private final Executor executor;
    private final RedissonClient redissonClient;

    public BatchJobProcessor(TransactionService transactionService, BatchJobRepository batchJobRepository, @Qualifier("fraudDetectionExecutor") Executor executor, RedissonClient redissonClient) {
        this.transactionService = transactionService;
        this.batchJobRepository = batchJobRepository;
        this.executor = executor;
        this.redissonClient = redissonClient;
    }

    @Async("fraudDetectionExecutor")
    public void processBatchJob(Long jobId, List<TransactionRequestDTO> transactions) {
        RLock rLock = redissonClient.getLock("batch-job-lock-" + jobId);
        try {
            boolean acquired = rLock.tryLock(0,30, TimeUnit.SECONDS);
            if(!acquired) {
                return;
            }
            BatchJob batchJob = batchJobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Batch Job Not Found"));
            batchJob.setStatus(BatchJobStatus.PROCESSING);
            batchJobRepository.save(batchJob);
            AtomicInteger processedCount = new AtomicInteger(0);
            AtomicInteger failedCount = new AtomicInteger(0);


            List<CompletableFuture<Void>> futures = transactions.stream()
                    .map(t -> CompletableFuture.runAsync( () -> {
                        try {
                            String key = UUID.randomUUID().toString();
                            transactionService.postTransaction(t.getSourceAccountId(), t.getDestinationAccountId(), t.getAmount(), t.getDescription(), key);
                            processedCount.incrementAndGet();
                        } catch (Exception ex) {
                            failedCount.incrementAndGet();
                        }
                    }, executor))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            batchJob.setProcessedTransactions(processedCount.get());
            batchJob.setFailedTransactions(failedCount.get());
            batchJob.setStatus(BatchJobStatus.COMPLETED);
            batchJob.setCompleteAt(LocalDateTime.now());
            batchJobRepository.save(batchJob);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if(rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }

    }
}
