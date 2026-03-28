package com.example.finflow.config;

import com.example.finflow.dto.TransactionEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {
    @Bean
    public KafkaTemplate<String, TransactionEvent> kafkaTemplate(ProducerFactory<String, TransactionEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic transactionCompletedTopic() {
        return TopicBuilder.name("transaction-completed")
                .partitions(1)
                .replicas(1)
                .build();
    }
}