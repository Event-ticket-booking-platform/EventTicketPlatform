package com.eventticketbooking.ticket.ticket_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eventticketbooking.ticket.ticket_service.entity.Ticket;
import com.eventticketbooking.ticket.ticket_service.entity.TicketEvent;
import com.eventticketbooking.ticket.ticket_service.kafka.*;
import com.eventticketbooking.ticket.ticket_service.service.TicketService;


@RestController
@RequestMapping("/tickets")
public class TicketController {
    
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Get all tickets for an event
    @GetMapping("/event/{eventId}")
    public List<Ticket> getTicketsByEvent(@PathVariable Long eventId) {
        return ticketService.getTicketsByEvent(eventId);
    }

    // Reserve seats
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveTicket(@RequestBody TicketReserveRequestedEvent request) {
        try {
            ticketService.reserveSeats(request);
            return ResponseEntity.ok("Seats reserved successfully! TicketReservedEvent published.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to reserve seats: " + e.getMessage());
        }
    }

    // Handle payment processed
    @PostMapping("/payment/processed")
    public String paymentProcessed(@RequestBody PaymentEvent paymentEvent) {
        ticketService.handlePaymentProcessed(paymentEvent);
        return "Payment processed event handled!";
    }

    // Handle payment failed
    @PostMapping("/payment/failed")
    public String paymentFailed(@RequestBody PaymentEvent paymentEvent) {
        ticketService.handlePaymentFailed(paymentEvent);
        return "Payment failed event handled!";
    }

    // Handle order cancelled
    @PostMapping("/order/canceled")
    public String orderCancelled(@RequestBody OrderCancelledEvent event) {
        ticketService.handleOrderCancelled(event);
        return "Order cancelled event handled!";
    }

    // Handle ticket expired
    @PostMapping("/ticket/expired")
    public String ticketExpired(@RequestBody TicketExpiredEvent event) {
        ticketService.handleTicketExpired(event);
        return "Ticket expired event handled!";
    }

    /// Create test event
    @PostMapping("/event/create")
    public ResponseEntity<String> createEvent(@RequestBody TicketEvent event) {
        try {
            ticketService.createEvent(event);
            return ResponseEntity.ok("Event created successfully with zero seats!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create event: " + e.getMessage());
        }
    }
    
    
}
