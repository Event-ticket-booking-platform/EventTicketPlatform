package com.eventticketbooking.ticket.ticket_service.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic ticketReservedTopic() {
        return TopicBuilder.name("ticket-reserved")
                .partitions(1)
                .replicas(1)
                .build();
    }
    
}
