package com.eventticketbooking.ticket.ticket_service.service;

import com.eventticketbooking.ticket.ticket_service.entity.Ticket;
import com.eventticketbooking.ticket.ticket_service.entity.TicketEvent;
import com.eventticketbooking.ticket.ticket_service.kafka.*;
import com.eventticketbooking.ticket.ticket_service.repository.TicketEventRepository;
import com.eventticketbooking.ticket.ticket_service.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {
     @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketEventRepository ticketEventRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketEventProducer ticketEventProducer;

    public List<Ticket> getTicketsByEvent(String eventId) {
        return ticketRepository.findByEventId(eventId);
    }

    public List<Ticket> getAvailableTickets(String eventId) {
        return ticketRepository.findByEventIdAndReservedFalse(eventId);
    }

    // Get total sold tickets across all events
    public long getTotalSoldTickets() {
        return ticketRepository.countByConfirmedTrue();
    }

    // Get sold tickets for a specific event
    public long getSoldTicketsByEvent(String eventId) {
        return ticketRepository.countByEventIdAndConfirmedTrue(eventId);
    }

   @Transactional
    public void reserveSeats(TicketReserveRequestedEvent event) {
    // Find event
    TicketEvent ticketEvent = ticketEventRepository.findById(String.valueOf(event.getEventId()))
            .orElseThrow(() -> new RuntimeException("Event not found"));

    // Generate tickets for the requested seats

    List<Long> ticketIds = new ArrayList<>();

    int startingSeatNumber = ticketEvent.getTotalSeats() + 1;
    for (int i = 0; i < event.getSeatCount(); i++) {
        Ticket ticket = new Ticket();
        ticket.setEventId(ticketEvent.getId());
        ticket.setSeatNumber(startingSeatNumber + i);
        ticket.setPrice(event.getTicketPrice());
        ticketRepository.save(ticket);
        ticketIds.add(ticket.getId()); 
    }

    // Update total seats
    ticketEvent.setTotalSeats(ticketEvent.getTotalSeats() + event.getSeatCount());
    ticketEventRepository.save(ticketEvent);

     TicketReservedEvent reservedEvent = new TicketReservedEvent(
            ticketIds,   
            event.getEventId(),
            event.getUserId(),
            event.getTicketPrice(),
            event.getSeatCount()
    );

    // Publish reserved event
    ticketEventProducer.sendTicketReserved(reservedEvent);
}


    @Transactional
    public void confirmReservation(String orderId, String paymentId) {
        List<Ticket> tickets = ticketRepository.findByOrderId(orderId);
        if (tickets.isEmpty()) return;

        tickets.forEach(ticket -> {
            ticket.setConfirmed(true);
            ticket.setConfirmedAt(LocalDateTime.now());
        });

        ticketRepository.saveAll(tickets);
        System.out.println("Confirmed reservation for orderId=" + orderId);
    }

    @Transactional
    public void releaseReservation(String orderId, String reason) {
        List<Ticket> tickets = ticketRepository.findByOrderId(orderId);
        if (tickets.isEmpty()) return;

        tickets.forEach(ticket -> {
            ticket.setReserved(false);
            ticket.setConfirmed(false);
            ticket.setUserId(null);
            ticket.setReservedAt(null);
            ticket.setConfirmedAt(null);
            ticket.setOrderId(null);
        });

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
        // publishToKafka("ticket.expired", event);
        ticketEventProducer.sendTicketExpired(event);
    }

    @Transactional
    public void createEvent(TicketEvent event) {
        TicketEvent ticketEvent = new TicketEvent();
        ticketEvent.setId(event.getId());
        ticketEvent.setTitle(event.getTitle());
        ticketEvent.setDescription(event.getDescription());
        ticketEvent.setLocation(event.getLocation());
        ticketEvent.setStartUtc(event.getStartUtc());
        ticketEvent.setEndUtc(event.getEndUtc());
        ticketEvent.setOrganizerId(event.getOrganizerId());

        // totalSeats will be initialized to 0 by default from the entity
        ticketEvent.setTotalSeats(0);

        ticketEventRepository.save(ticketEvent);
    }



    private void publishToKafka(String topic, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
