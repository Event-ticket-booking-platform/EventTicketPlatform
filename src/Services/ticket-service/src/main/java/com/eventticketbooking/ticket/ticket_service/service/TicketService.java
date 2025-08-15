package com.eventticketbooking.ticket.ticket_service.service;

import java.util.List;

// import com.eventticketbooking.ticket.ticket_service.dto.ReserveTicketRequest;
import com.eventticketbooking.ticket.ticket_service.entity.Ticket;

public interface TicketService {
    List<Ticket> getTicketsByEvent(Long eventId);
    // void reserveTicket(ReserveTicketRequest request);\
    // Ticket reserveTicket(ReserveTicketRequest request);
    Ticket reserveTicket(Long eventId, String seatNumber, Long userId);
    List<Ticket> getAvailableTickets(Long eventId);
    boolean isSeatAvailable(Long eventId, String seatNumber);
    
}
