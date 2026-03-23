package com.example.finflow.service;

import com.example.finflow.dto.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FraudDetectionConsumer {
    private final FraudDetectionService fraudDetectionService;
    private static final Logger log = LoggerFactory.getLogger(FraudDetectionConsumer.class);

    public FraudDetectionConsumer(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @KafkaListener(topics = "transaction-completed", groupId = "finflow-group")
    public void handleTransactionEvent(TransactionEvent event) {
        fraudDetectionService.analyzeTransaction(event)
                .thenAccept(transaction -> {
                    if(transaction) {
                        log.warn("Transaction Fraud detected for transaction {}", event.getTransactionId());
                    }
                });
    }
}
