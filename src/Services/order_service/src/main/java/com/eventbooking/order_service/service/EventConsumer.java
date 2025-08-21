package com.eventbooking.order_service.service;

import com.eventbooking.order_service.dto.PaymentEvent;
import com.eventbooking.order_service.dto.TicketExpiredEvent;
import com.eventbooking.order_service.dto.TicketReserved;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {

    private final ObjectMapper mapper;

    private final OrderService orderService;
    private final EventProducer eventProducer;

    @Autowired
    public EventConsumer(ObjectMapper mapper, OrderService orderService, EventProducer eventProducer) {
        this.mapper = mapper;
        this.orderService = orderService;
        this.eventProducer = eventProducer;
    }

    @KafkaListener(topics = "ticket.reserved", groupId = "order-service")
    public void handleTicketReserved(String message) {
        try {
            TicketReserved event = mapper.readValue(message, TicketReserved.class);
            System.out.println("####: Ticket Reserved Event: " + message);

            if(orderService.isTicketReservedValid(event)) {
                orderService.createOrder(event, false);
                System.out.printf("####: Order Processed from TicketReserved event: " + event);
            } else {
                System.out.println("####: Invalid data: " + message);
                eventProducer.sendOrderFailedEvent(message);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            eventProducer.sendOrderFailedEvent(message);
        }
    }

    @KafkaListener(topics = "payment.processed", groupId = "order-service")
    public void handlePaymentSuccessful(String message) {
        try {
            PaymentEvent event = mapper.readValue(message, PaymentEvent.class);
            System.out.println("####: Payment Processed Event: " + message);

            if(isPaymentValid(event)) {
                orderService.handlePaymentProcessed(event);
                System.out.println("####: Order Processed from Payment event: " + event);
            } else {
                System.out.println("####: Invalid data: " + event);
                eventProducer.sendPaymentFailedEvent(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @KafkaListener(topics = "ticket.expired", groupId = "order-service")
    public void handleTicketExpired(String message) {
        try {
            TicketExpiredEvent event = mapper.readValue(message, TicketExpiredEvent.class);
            System.out.println("Ticket Expired Event: " + message);

            if(isTicketExpiredValid(event)) {
                orderService.handleTicketExpired(event);
                System.out.println("Order with order ID: " + event.getOrderId() + "got cancelled due to ticket expire");
            } else {
                System.out.println("Invalid data: " + event);
                eventProducer.sendTicketCancelFailedEvent(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isTicketExpiredValid(TicketExpiredEvent event) {
        return event.getTicketId() != null && !event.getTicketId().isEmpty()
                && event.getOrderId() != null && !event.getOrderId().isEmpty()
                && event.getUserId() != null && !event.getUserId().isEmpty()
                && event.getExpirationTime() != null
                && event.getReason() != null && !event.getReason().isEmpty();
    }

    private boolean isPaymentValid(PaymentEvent event) {
        return event.getPaymentId() != null && !event.getPaymentId().isEmpty()
                && event.getOrderId() != null && !event.getOrderId().isEmpty()
                && event.getStatus() != null && !event.getStatus().isEmpty()
                && event.getAmount() != null;
    }
}
