package com.eventticketbooking.ticket.ticket_service.controller;

// package main.java.com.eventticketbooking.ticket.ticket_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventticketbooking.ticket.ticket_service.dto.ReserveTicketRequest;
// import com.eventticketbooking.ticket.ticket_service.dto.ReserveTicketRequest;
import com.eventticketbooking.ticket.ticket_service.entity.Ticket;
import com.eventticketbooking.ticket.ticket_service.kafka.TicketEventProducer;
import com.eventticketbooking.ticket.ticket_service.kafka.TicketReservedEvent;
import com.eventticketbooking.ticket.ticket_service.service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;
    private TicketEventProducer ticketEventProducer;

    public TicketController(TicketEventProducer ticketEventProducer) {
        this.ticketEventProducer = ticketEventProducer;
    }

    @GetMapping("/event/{eventId}")
    public List<Ticket> getTicketsByEventId(@PathVariable Long eventId) {
        return ticketService.getTicketsByEvent(eventId);
    }

    @GetMapping("/event/{eventId}/available")
    public List<Ticket> getAvailableTickets(@PathVariable Long eventId) {
        return ticketService.getAvailableTickets(eventId);
    }


    @PostMapping("/reserve")
    public String reserveTicket(@RequestBody ReserveTicketRequest request) {
    if (!ticketService.isSeatAvailable(request.getEventId(), request.getSeatNumber())) {
        return "Seat is not available!";
    }

    // reserve in DB first
    Ticket ticket = ticketService.reserveTicket(
            request.getEventId(),
            request.getSeatNumber(),
            request.getUserId()
    );

    // Build event for Order Service
    TicketReservedEvent event = new TicketReservedEvent(
            ticket.getId().toString(),
            ticket.getUserId().toString(),
            ticket.getEventId().toString(),
            1,              // quantity (1 seat reserved)
            100.0,          // price (TODO: set real price from DB)
            1               // showNumber (can extend if multiple shows)
    );

    ticketEventProducer.sendMessage(event);

    return "Reservation event sent to Kafka!";
    }


    
}
