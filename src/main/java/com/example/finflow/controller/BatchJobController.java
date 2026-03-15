package com.example.finflow.controller;

import com.example.finflow.dto.TransactionRequestDTO;
import com.example.finflow.entity.BatchJob;
import com.example.finflow.entity.BatchJobStatus;
import com.example.finflow.service.BatchJobService;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batch")
public class BatchJobController {
    private BatchJobService batchJobService;
    public BatchJobController(BatchJobService batchJobService) {
        this.batchJobService = batchJobService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<Long> createBatchJob(@RequestBody List<TransactionRequestDTO> transactions) {
        Long id = batchJobService.createBatchJob(transactions);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<BatchJob>  getBatchJobStatus(@PathVariable("jobId") Long jobId) {
        BatchJob batchJob = batchJobService.getBatchJob(jobId);
        return ResponseEntity.status(HttpStatus.OK).body(batchJob);
    }
}
