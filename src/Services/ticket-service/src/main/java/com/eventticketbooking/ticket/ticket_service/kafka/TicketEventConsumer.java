package com.eventticketbooking.ticket.ticket_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.eventticketbooking.ticket.ticket_service.service.TicketService;


@Service
public class TicketEventConsumer {
    @Autowired
    private TicketService ticketService;

    @KafkaListener(topics = "ticket.reserved", groupId = "ticket-service-group")
    public void consume(TicketReservedEvent event) {
        System.out.println("Received reservation event for seat: " + event.getShowNumber());
        ticketService.reserveTicket(
                Long.parseLong(event.getEventId()),
                String.valueOf(event.getShowNumber()),
                Long.parseLong(event.getUserId())
        );
    }

    @KafkaListener(topics = "ticket.reserved", groupId = "ticket-service-group")
    public void consumeRaw(String message) {
    System.out.println("RAW Kafka message: " + message);
    }

    
}
