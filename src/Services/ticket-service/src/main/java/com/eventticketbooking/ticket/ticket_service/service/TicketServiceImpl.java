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
    private final TicketEventProducer ticketEventProducer;

    public TicketServiceImpl(TicketRepository ticketRepository, TicketEventProducer ticketEventProducer) {
        this.ticketRepository = ticketRepository;
        this.ticketEventProducer = ticketEventProducer;
    }

     @Override
    public List<Ticket> getTicketsByEvent(Long eventId) {
        return ticketRepository.findByEventId(eventId);
    }

    public Ticket reserveTicket(Long eventId, String seatNumber, Long userId) {
        Ticket ticket = ticketRepository.findByEventIdAndSeatNumber(eventId, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (ticket.isReserved()) {
            throw new RuntimeException("Seat already reserved");
        }

        ticket.setReserved(true);
        ticket.setUserId(userId);
        ticket.setReservedAt(LocalDateTime.now());
        ticket.setOrderId(UUID.randomUUID().toString());

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

    @Override
    public void confirmReservation(String orderId, String paymentId) {
        List<Ticket> tickets = ticketRepository.findByOrderId(orderId);
        for (Ticket ticket : tickets) {
            ticket.setConfirmed(true);
            ticket.setConfirmedAt(LocalDateTime.now());
            ticketRepository.save(ticket);
        }
        System.out.println("Reservation confirmed for order: " + orderId);
    }

    @Override
    public void releaseReservation(String orderId, String reason) {
        List<Ticket> tickets = ticketRepository.findByOrderId(orderId);
        for (Ticket ticket : tickets) {
            ticket.setReserved(false);
            ticket.setUserId(null);
            ticket.setReservedAt(null);
            ticket.setOrderId(null);
            ticketRepository.save(ticket);
        }
        System.out.println("Released reservation for order: " + orderId + " Reason: " + reason);
    }

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void checkExpiredReservations() {
        List<Ticket> expiredTickets = ticketRepository.findExpiredReservations();
        for (Ticket ticket : expiredTickets) {
            TicketExpiredEvent event = new TicketExpiredEvent(
                String.valueOf(ticket.getId()),
                ticket.getOrderId(),
                String.valueOf(ticket.getUserId()),
                LocalDateTime.now(),
                "RESERVATION_TIMEOUT"
            );
            ticketEventProducer.sendTicketExpired(event);
            releaseReservation(ticket.getOrderId(), "RESERVATION_TIMEOUT");
        }
    }
}
