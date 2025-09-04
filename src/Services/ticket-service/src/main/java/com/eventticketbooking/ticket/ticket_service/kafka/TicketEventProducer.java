package com.eventticketbooking.ticket.ticket_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TicketEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TicketEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(TicketReservedEvent event) {
        sendMessageToTopic(event, "ticket.reserved");
    }

    public void sendTicketExpired(TicketExpiredEvent event) {
        sendMessageToTopic(event, "ticket.expired");
    }

     public void sendMessageToTopic(Object event, String topic) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, json);
            System.out.println("Sent event to Kafka topic " + topic + ": " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
