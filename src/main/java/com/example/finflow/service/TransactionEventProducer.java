package com.example.finflow.service;

import com.example.finflow.dto.TransactionEvent;
import com.example.finflow.entity.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionEventProducer {
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionEventProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(Transaction transaction) {
        TransactionEvent event = new TransactionEvent(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getTimestamp(),
                transaction.getAccount().getId()
        );

        kafkaTemplate.send("transaction-completed", String.valueOf(event.getAccountId()), event);
    }
}