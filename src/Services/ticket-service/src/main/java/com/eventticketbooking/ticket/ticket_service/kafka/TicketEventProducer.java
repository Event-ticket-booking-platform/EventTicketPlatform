package com.eventticketbooking.ticket.ticket_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TicketEventProducer {
    private static final String TOPIC = "ticket.reserved";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TicketEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(TicketReservedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, json);
            System.out.println("Sent TicketReservedEvent to Kafka: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTicketExpired(TicketExpiredEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("ticket.expired", json);
            System.out.println("Sent TicketExpiredEvent to Kafka: " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
