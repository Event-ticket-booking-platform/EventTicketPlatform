package com.eventticketbooking.ticket.ticket_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

// import com.eventticketbooking.ticket.ticket_service.dto.ReserveTicketRequest;
import com.eventticketbooking.ticket.ticket_service.entity.Ticket;
import com.eventticketbooking.ticket.ticket_service.repository.TicketRepository;

@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    // private final TicketEventProducer ticketEventProducer;

    // public TicketServiceImpl(TicketRepository ticketRepository, TicketEventProducer ticketEventProducer) {
    //     this.ticketRepository = ticketRepository;
    //     this.ticketEventProducer = ticketEventProducer;
    // }

     public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

     @Override
    public List<Ticket> getTicketsByEvent(Long eventId) {
        return ticketRepository.findByEventId(eventId);
    }

    // @Override
    // public Ticket reserveTicket(ReserveTicketRequest request) {
    //     Ticket ticket = ticketRepository.findByEventIdAndSeatNumber(request.getEventId(), request.getSeatNumber())
    //             .orElseThrow(() -> new RuntimeException("Seat not found for event"));

    //     if (ticket.isReserved()) {
    //         throw new RuntimeException("Seat already reserved");
    //     }

    //     ticket.setReserved(true);
    //     ticket.setUserId(request.getUserId());
    //     ticket.setReservedAt(LocalDateTime.now());
    //     return ticketRepository.save(ticket);
    // }

        public Ticket reserveTicket(Long eventId, String seatNumber, Long userId) {
        Ticket ticket = ticketRepository.findByEventIdAndSeatNumber(eventId, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (ticket.isReserved()) {
            throw new RuntimeException("Seat already reserved");
        }

        ticket.setReserved(true);
        ticket.setUserId(userId);
        ticket.setReservedAt(LocalDateTime.now());

        return ticketRepository.save(ticket);
    }


      @Override
     public List<Ticket> getAvailableTickets(Long eventId) {
        return ticketRepository.findByEventIdAndReservedFalse(eventId);
    }    
    
    @Override
    public boolean isSeatAvailable(Long eventId, String seatNumber) {
        return ticketRepository.findByEventIdAndSeatNumber(eventId, seatNumber)
                .map(ticket -> !ticket.isReserved())
                .orElse(false);
    }
}
