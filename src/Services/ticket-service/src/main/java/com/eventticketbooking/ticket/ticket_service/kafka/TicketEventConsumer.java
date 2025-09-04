package com.eventticketbooking.ticket.ticket_service.kafka;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.eventticketbooking.ticket.ticket_service.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eventticketbooking.ticket.ticket_service.entity.EventShow;
import com.eventticketbooking.ticket.ticket_service.entity.Seat;
import com.eventticketbooking.ticket.ticket_service.entity.Ticket;
import com.eventticketbooking.ticket.ticket_service.entity.TicketEvent;
import com.eventticketbooking.ticket.ticket_service.repository.EventShowRepository;
import com.eventticketbooking.ticket.ticket_service.repository.SeatRepository;
import com.eventticketbooking.ticket.ticket_service.repository.TicketEventRepository;
import com.eventticketbooking.ticket.ticket_service.repository.TicketRepository;

@Service
public class TicketEventConsumer {
    @Autowired private TicketEventRepository ticketEventRepository;
    @Autowired private EventShowRepository eventShowRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private TicketService ticketService;
    @Autowired private ObjectMapper objectMapper;
    

    @KafkaListener(topics = "event-created")
    public void consumeEventCreated(String message) {
        try {
            EventCreatedEvent event = objectMapper.readValue(message, EventCreatedEvent.class);
            System.out.println("Received new event: " + event.getTitle());

            TicketEvent ticketEvent = new TicketEvent();
            ticketEvent.setTitle(event.getTitle());
            ticketEvent.setDescription(event.getDescription());
            ticketEvent.setLocation(event.getLocation());
            ticketEvent.setStartUtc(event.getStartUtc());
            ticketEvent.setEndUtc(event.getEndUtc());
            ticketEvent.setOrganizerId(event.getOrganizerId());
            ticketEvent = ticketEventRepository.save(ticketEvent);

            EventShow show = new EventShow();
            show.setEvent(ticketEvent);
            show.setShowNumber(1);
            show.setStartTime(LocalDateTime.ofInstant(event.getStartUtc(), ZoneId.systemDefault()));
            show.setEndTime(LocalDateTime.ofInstant(event.getEndUtc(), ZoneId.systemDefault()));
            show = eventShowRepository.save(show);

            for (int i = 1; i <= 20; i++) {
                String seatNumber = "A" + i;

                Seat seat = new Seat();
                seat.setShow(show);
                seat.setSeatNumber(seatNumber);
                seatRepository.save(seat);

                Ticket ticket = new Ticket();
                ticket.setEventId(ticketEvent.getId());
                ticket.setShowId(show.getId());
                ticket.setSeatNumber(seatNumber);
                ticketRepository.save(ticket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @KafkaListener(topics = "ticket.reserve.requested", groupId = "ticket-service-group")
    public void consumeTicketReserveRequested(String message) {
        try {
            TicketReserveRequestedEvent event = objectMapper.readValue(message, TicketReserveRequestedEvent.class);
            ticketService.reserveSeats(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "payment.processed", groupId = "ticket-service-group")
    public void consumePaymentProcessed(String message) {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
            System.out.println("Payment processed: " + event);
            ticketService.confirmReservation(event.getOrderId(), event.getPaymentId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "ticket-service-group")
    public void consumePaymentFailed(String message) {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
            System.out.println("Payment failed: " + event);
            ticketService.releaseReservation(event.getOrderId(), "PAYMENT_FAILED");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

     @KafkaListener(topics = "order.canceled", groupId = "ticket-service-group")
    public void consumeOrderCancelled(String message) {
        try {
            OrderCancelledEvent event = objectMapper.readValue(message, OrderCancelledEvent.class);
            System.out.println("Order canceled: " + event);
            ticketService.releaseReservation(event.getOrderId(), event.getCancelReason());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    
}