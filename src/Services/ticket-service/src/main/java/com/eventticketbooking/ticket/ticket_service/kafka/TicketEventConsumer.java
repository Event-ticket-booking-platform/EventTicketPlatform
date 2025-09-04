package com.eventticketbooking.ticket.ticket_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.eventticketbooking.ticket.ticket_service.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TicketEventConsumer {
    @Autowired private TicketService ticketService;
    @Autowired private ObjectMapper objectMapper;
    

    @KafkaListener(topics = "event-created")
    public void consumeEventCreated(String message) {
        try {
            EventCreatedEvent event = objectMapper.readValue(message, EventCreatedEvent.class);
            System.out.println("Received new event: " + event.getTitle());
            ticketService.createEventWithSeats(event);
            
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