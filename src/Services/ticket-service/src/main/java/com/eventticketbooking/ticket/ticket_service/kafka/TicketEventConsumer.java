package com.eventticketbooking.ticket.ticket_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.eventticketbooking.ticket.ticket_service.entity.TicketEvent;
import com.eventticketbooking.ticket.ticket_service.service.TicketService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TicketEventConsumer {
    @Autowired private TicketService ticketService;
    @Autowired private ObjectMapper objectMapper;
    

    @KafkaListener(topics = "event-created", groupId = "ticket-service-group")
    public void consumeEventCreated(String message) throws JsonProcessingException {
        try {
            System.out.println("Raw message received: " + message);  // log raw payload
            TicketEvent event = objectMapper.readValue(message, TicketEvent.class);
            System.out.println("Deserialized event: " + event);
            ticketService.createEvent(event);
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @KafkaListener(topics = "event.catelog.upsert.v1", groupId = "ticket-service-group")
    public void consumeTicketReserveRequested(String message) throws JsonProcessingException {
        try {
            TicketReserveRequestedEvent event = objectMapper.readValue(message, TicketReserveRequestedEvent.class);
            ticketService.reserveSeats(event);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "payment.processed", groupId = "ticket-service-group")
    public void consumePaymentProcessed(String message)throws JsonProcessingException {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
            System.out.println("Payment processed: " + event);
            ticketService.confirmReservation(event.getOrderId(), event.getPaymentId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "ticket-service-group")
    public void consumePaymentFailed(String message) throws JsonProcessingException {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
            System.out.println("Payment failed: " + event);
            ticketService.releaseReservation(event.getOrderId(), "PAYMENT_FAILED");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

     @KafkaListener(topics = "order.canceled", groupId = "ticket-service-group")
    public void consumeOrderCancelled(String message) throws JsonProcessingException {
        try {
            OrderCancelledEvent event = objectMapper.readValue(message, OrderCancelledEvent.class);
            System.out.println("Order canceled: " + event);
            ticketService.releaseReservation(event.getOrderId(), event.getCancelReason());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    
}