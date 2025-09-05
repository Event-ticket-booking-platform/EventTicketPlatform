package com.eventticketbooking.ticket.ticket_service.controller;

// package main.java.com.eventticketbooking.ticket.ticket_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventticketbooking.ticket.ticket_service.entity.Ticket;
import com.eventticketbooking.ticket.ticket_service.kafka.PaymentEvent;
import com.eventticketbooking.ticket.ticket_service.kafka.EventCreatedEvent;
import com.eventticketbooking.ticket.ticket_service.kafka.OrderCancelledEvent;
import com.eventticketbooking.ticket.ticket_service.kafka.TicketExpiredEvent;

import com.eventticketbooking.ticket.ticket_service.service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/event/{eventId}")
    public List<Ticket> getTicketsByEventId(@PathVariable Long eventId) {
        return ticketService.getTicketsByEvent(eventId);
    }

    @GetMapping("/event/{eventId}/available")
    public List<Ticket> getAvailableTickets(@PathVariable Long eventId) {
        return ticketService.getAvailableTickets(eventId);
    }

    // @PostMapping("/reserve")
    // public String reserveTicket(@RequestBody ReserveTicketRequest request) {
    //     boolean reserved = ticketService.reserveAndPublishEvent(
    //             request.getEventId(),
    //             request.getSeatNumber(),
    //             request.getUserId()
    //     );

    //     return reserved ? "Reservation event sent to Kafka!" : "Seat is not available!";
    // }

    @PostMapping("/payment/processed")
    public String paymentProcessed(@RequestBody PaymentEvent paymentEvent) {
        ticketService.handlePaymentProcessed(paymentEvent);
        return "Payment processed event handled!";
    }

    @PostMapping("/payment/failed")
    public String paymentFailed(@RequestBody PaymentEvent paymentEvent) {
        ticketService.handlePaymentFailed(paymentEvent);
        return "Payment failed event handled!";
    }

    @PostMapping("/order/canceled")
    public String orderCancelled(@RequestBody OrderCancelledEvent event) {
        ticketService.handleOrderCancelled(event);
        return "Order cancelled event handled!";
    }

    @PostMapping("/ticket/expired")
    public String ticketExpired(@RequestBody TicketExpiredEvent event) {
        ticketService.handleTicketExpired(event);
        return "Ticket expired event handled!";
    }

    @PostMapping("/test-new-event")
    public ResponseEntity<String> createTestEvent(@RequestBody EventCreatedEvent event) {
        try {
            ticketService.createEventWithSeats(event);
            return ResponseEntity.ok("Test event created successfully with seats & tickets!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to create test event: " + e.getMessage());
        }
    }
    // {
    //     "title": "Test Concert",
    //     "description": "A special test event",
    //     "location": "Colombo Stadium",
    //     "startUtc": "2025-09-10T14:00:00Z",
    //     "endUtc": "2025-09-10T17:00:00Z",
    //     "organizerId": 101
    // }   

    @GetMapping("/sold/total")
    public long getTotalSoldTickets() {
        return ticketService.getTotalSoldTickets();
    }

    @GetMapping("/event/{eventId}/sold")
    public long getSoldTicketsByEvent(@PathVariable Long eventId) {
        return ticketService.getSoldTicketsByEvent(eventId);
    }
    
}
