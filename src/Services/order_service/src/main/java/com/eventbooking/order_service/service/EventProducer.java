package com.eventbooking.order_service.service;

import com.eventbooking.order_service.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class EventProducer {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    public void sendOrderCreatedEvent(OrderEvent event) throws JsonProcessingException { // an order was created
        System.out.println("####: Sending order");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("order.created", json); // For payment service
    }

    public void sendOrderSuccessfulEvent(OrderEvent event) throws JsonProcessingException {
        System.out.println("####: Successful order");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("order.successful", json);
    }

    public void sendOrderCancelledEvent(OrderCancelledEvent event) throws JsonProcessingException { // For cancelling
        System.out.println("Cancelled order");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("order.cancelled", json);
    }

    public void sendOrderErrorEvent(String event) { // For failing (1). Could not place the order
        System.out.println("Failed order");
        kafkaTemplate.send("ticketReserve.error", event);
    }

    public void sendOrderFailedEvent(OrderEvent event) throws JsonProcessingException { // (2) Payment failed
        System.out.println("####: Failed order");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("order.failed", json);
    }

    public void sendErrorEvent(ErrorEvent errorEvent) throws JsonProcessingException {
        System.out.println("####: Order not found");
        String json = mapper.writeValueAsString(errorEvent);
        kafkaTemplate.send("error", json);
    }

    public void sendPaymentProcessingFailedEvent(String message) {
        System.out.println("Failed payment");
        kafkaTemplate.send("paymentProcessing.failed", message);
    }

    public void sendTicketCancelFailedEvent(TicketExpiredEvent event) throws JsonProcessingException {
        System.out.println("Failed ticket cancel");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("ticketCancel.failed", json);
    }

    public void sendTicketCancelErrorEvent(String message) {
        System.out.println("Failed ticket cancel due to invalid format");
        kafkaTemplate.send("ticketCancel.error", message);
    }

    public void sendOrderCancellingErrorEvent(String message) {
        System.out.println("Failed order cancel due to invalid format");
        kafkaTemplate.send("orderCancelling.error", message);
    }

    public void sendOrderAlreadyCancelledEvent(TicketReserved request) throws JsonProcessingException {
        System.out.println("Failed ticket cancel");
        String json = mapper.writeValueAsString(request);
        kafkaTemplate.send("orderAlready.cancelled", json);
    }
}
