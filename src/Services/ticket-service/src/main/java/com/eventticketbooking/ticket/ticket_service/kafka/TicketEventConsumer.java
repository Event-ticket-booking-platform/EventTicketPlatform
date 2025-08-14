package com.eventticketbooking.ticket.ticket_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.eventticketbooking.ticket.ticket_service.service.TicketService;


@Service
public class TicketEventConsumer {

    // @KafkaListener(topics = "ticket-reserved", groupId = "ticket-service-group")
    // public void consume(TicketReservedEvent event) {
    //     System.out.println("Received ticket reservation event: " + event.getSeatNumber());
    // }

    @Autowired
    private TicketService ticketService;

    @KafkaListener(topics = "ticket-reserved", groupId = "ticket-service-group")
    public void consume(TicketReservedEvent event) {
        System.out.println("Received reservation event for seat: " + event.getSeatNumber());
        ticketService.reserveTicket(
                Long.parseLong(event.getEventId()),
                event.getSeatNumber(),
                Long.parseLong(event.getUserId())
        );
    }

    
}
