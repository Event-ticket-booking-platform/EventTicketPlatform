package com.eventticketbooking.ticket.ticket_service.service;

import com.eventticketbooking.ticket.ticket_service.entity.Ticket;
import com.eventticketbooking.ticket.ticket_service.kafka.*;
import com.eventticketbooking.ticket.ticket_service.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Ticket> getTicketsByEvent(Long eventId) {
        return ticketRepository.findByEventId(eventId);
    }

    public List<Ticket> getAvailableTickets(Long eventId) {
        return ticketRepository.findByEventIdAndReservedFalse(eventId);
    }

    @Transactional
    public void reserveSeats(TicketReserveRequestedEvent event) {
        List<Ticket> tickets = ticketRepository
                .findByEventIdAndShowIdAndSeatNumberIn(event.getEventId(), event.getShowId(), event.getSeatNumbers());

        for (Ticket ticket : tickets) {
            if (ticket.isReserved()) {
                throw new RuntimeException("Seat already reserved: " + ticket.getSeatNumber());
            }
            ticket.setReserved(true);
            ticket.setUserId(event.getUserId());
            ticket.setReservedAt(LocalDateTime.now());
        }

        ticketRepository.saveAll(tickets);

        // Publish "ticket.reserved"
        try {
            TicketReservedEvent reservedEvent = new TicketReservedEvent();
            reservedEvent.setOrderId(event.getOrderId());
            reservedEvent.setEventId(event.getEventId());
            reservedEvent.setShowId(event.getShowId());
            reservedEvent.setSeatNumbers(event.getSeatNumbers());
            reservedEvent.setUserId(event.getUserId());

            String json = objectMapper.writeValueAsString(reservedEvent);
            kafkaTemplate.send("ticket.reserved", json);
            System.out.println("Published ticket.reserved for orderId=" + event.getOrderId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish ticket.reserved", e);
        }
    }


    @Transactional
    public void confirmReservation(String orderId, String paymentId) {
        List<Ticket> tickets = ticketRepository.findByOrderId(orderId); 
      

        if (tickets.isEmpty()) {
            throw new RuntimeException("No reserved tickets found for orderId: " + orderId);
        }

        for (Ticket ticket : tickets) {
            if (!ticket.isReserved()) {
                throw new RuntimeException("Ticket is not reserved: " + ticket.getSeatNumber());
            }
            ticket.setConfirmed(true);
            ticket.setConfirmedAt(LocalDateTime.now());
        }

        ticketRepository.saveAll(tickets);
        System.out.println("Confirmed reservation for orderId=" + orderId + ", paymentId=" + paymentId);
    }

    @Transactional
    public void releaseReservation(String orderId, String reason) {
        // Find tickets by orderId (or by userId depending on your schema)
        List<Ticket> tickets = ticketRepository.findByOrderId(orderId);

        if (tickets.isEmpty()) {
            System.out.println("No tickets found to release for orderId=" + orderId);
            return;
        }

        for (Ticket ticket : tickets) {
            ticket.setReserved(false);
            ticket.setUserId(null);
            ticket.setReservedAt(null);
            ticket.setConfirmed(false);
            ticket.setConfirmedAt(null);
        }

        ticketRepository.saveAll(tickets);
        System.out.println("Released tickets for orderId=" + orderId + ", reason=" + reason);
    }

    public void handlePaymentProcessed(PaymentEvent event) {
        confirmReservation(event.getOrderId(), event.getPaymentId());
        publishToKafka("payment.processed", event);
    }

    public void handlePaymentFailed(PaymentEvent event) {
        releaseReservation(event.getOrderId(), "PAYMENT_FAILED");
        publishToKafka("payment.failed", event);
    }

    public void handleOrderCancelled(OrderCancelledEvent event) {
        releaseReservation(event.getOrderId(), event.getCancelReason());
        publishToKafka("order.canceled", event);
    }

    public void handleTicketExpired(TicketExpiredEvent event) {
        releaseReservation(event.getOrderId(), "TICKET_EXPIRED");
        publishToKafka("ticket.expired", event);
    }

    // ---------------- Helper ----------------

    private void publishToKafka(String topic, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, json);
            System.out.println("📤 Published " + topic + " event");
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish " + topic, e);
        }
    }
    
}
