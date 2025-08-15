package com.eventticketbooking.ticket.ticket_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TicketEventProducer {
    // private static final String TOPIC = "ticket-reserved";

    // private final KafkaTemplate<String, String> kafkaTemplate;

    // public TicketEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
    //     this.kafkaTemplate = kafkaTemplate;
    // }

    // public void sendMessage(String message) {
    //     kafkaTemplate.send(TOPIC, message);
    //     System.out.println("Sent message to Kafka: " + message);
    // }
    
    private static final String TOPIC = "ticket-reserved";
    private final KafkaTemplate<String, TicketReservedEvent> kafkaTemplate;

    public TicketEventProducer(KafkaTemplate<String, TicketReservedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(TicketReservedEvent event) {
        kafkaTemplate.send(TOPIC, event);
        System.out.println("Sent message to Kafka: " + event);
    }
}
