package com.eventbooking.order_service.service;

import com.eventbooking.order_service.dto.PaymentEvent;
import com.eventbooking.order_service.dto.TicketExpiredEvent;
import com.eventbooking.order_service.dto.TicketReservedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {

    private final ObjectMapper mapper;

    private final OrderService orderService;

    @Autowired
    public EventConsumer(ObjectMapper mapper, OrderService orderService) {
        this.mapper = mapper;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "ticket.reserved", groupId = "order-service")
    public void handleTicketReserved(String message) throws JsonProcessingException {
        try {
            TicketReservedEvent event = mapper.readValue(message, TicketReservedEvent.class);
            System.out.println("####: Ticket Reserved Event: " + message);

            if(isTicketReservedValid(event)) {
                orderService.createOrder(event);
                System.out.printf("####: Order Processed from TicketReserved event: " + event);
            } else {
                System.out.println("####: Invalid data: " + event); // TODO: Should we publish an event?
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @KafkaListener(topics = "payment.processed", groupId = "order-service")
    public void handlePaymentSuccessful(String message) throws JsonProcessingException {
        try {
            PaymentEvent event = mapper.readValue(message, PaymentEvent.class);
            System.out.println("####: Payment Processed Event: " + message);

            if(isPaymentValid(event)) {
                orderService.handlePaymentProcessed(event);
                System.out.println("####: Order Processed from Payment event: " + event);
            } else {
                System.out.println("####: Invalid data: " + event); // TODO: Should we publish an event?
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @KafkaListener(topics = "ticket.expired", groupId = "order-service")
    public void handleTicketExpired(String message) throws JsonProcessingException {
        try {
            TicketExpiredEvent event = mapper.readValue(message, TicketExpiredEvent.class);
            System.out.println("Ticket Expired Event: " + message);

            if(isTicketExpiredValid(event)) {
                orderService.handleTicketTicketExpired(event);
                System.out.println("Order with order ID: " + event.getOrderId() + "got cancelled due to ticket expire");
            } else {
                System.out.println("Invalid data: " + event); // TODO: Should we publish an event?
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isTicketExpiredValid(TicketExpiredEvent event) {
        return event.getTicketId() != null && !event.getTicketId().isEmpty()
                && event.getOrderId() != null && !event.getOrderId().isEmpty()
                && event.getUserId() != null && !event.getUserId().isEmpty()
                && event.getExpirationTime() != null && !event.getExpirationTime().isEmpty()
                && event.getReason() != null && !event.getReason().isEmpty();
    }

    private boolean isTicketReservedValid(TicketReservedEvent event) {
        System.out.println("Validating");
        return event.getTicketId() != null && !event.getTicketId().isEmpty()
                && event.getUserId() != null && !event.getUserId().isEmpty()
                && event.getEventId() != null && !event.getEventId().isEmpty() // TODO: Validate all fields
                && event.getShowNumber() > 0;
    }

    private boolean isPaymentValid(PaymentEvent event) {
        return event.getPaymentId() != null && !event.getPaymentId().isEmpty()
                && event.getOrderId() != null && !event.getOrderId().isEmpty()
                && event.getStatus() != null && !event.getStatus().isEmpty()
                && event.getAmount() != null;
    }

//    @KafkaListener(topics = "paymentConfirmed", groupId = "order-service")
//    public void handlePaymentConfirmed(String message) throws JsonProcessingException {
//        PaymentConfirmedEvent event = mapper.readValue(message, PaymentConfirmedEvent.class);
//        System.out.println("Payment Confirmed Event: " + event);
//    }
//
//    @KafkaListener(topics = "ticketReleased", groupId = "order-service")
//    public void handleTicketReleased(String message) throws JsonProcessingException {
//        TicketReleasedEvent event = mapper.readValue(message, TicketReleasedEvent.class);
//        System.out.println("Ticket Released Event: " + event);
//    }
}
