package com.eventticketbooking.ticket.ticket_service.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic ticketReservedTopic() {
        return TopicBuilder.name("ticket.reserved")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentProcessedTopic() {
        return TopicBuilder.name("payment.processed")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name("payment.failed")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ticketExpiredTopic() {
        return TopicBuilder.name("ticket.expired")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCanceledTopic() {
        return TopicBuilder.name("order.canceled")
                .partitions(1)
                .replicas(1)
                .build();
    }
    
}
