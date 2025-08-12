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
import com.eventticketbooking.ticket.ticket_service.entity.Ticket;
import com.eventticketbooking.ticket.ticket_service.service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping("/event/{eventId}")
    public List<Ticket> getTicketsByEventId(@PathVariable Long eventId) {
        return ticketService.getTicketsByEvent(eventId);
    }

    @GetMapping("/event/{eventId}/available")
    public List<Ticket> getAvailableTickets(@PathVariable Long eventId) {
        return ticketService.getAvailableTickets(eventId);
    }

    @PostMapping("/reserve")
    public Ticket reserveTicket(@RequestBody ReserveTicketRequest request) {
        return ticketService.reserveTicket(request);
    }
    
}
