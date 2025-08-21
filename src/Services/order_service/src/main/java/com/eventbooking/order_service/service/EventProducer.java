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

    public void sendOrderCreatedEvent(OrderEvent event) throws JsonProcessingException {
        System.out.println("####: Sending order");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("order.created", json); // For payment service
    }

    public void sendOrderSuccessfulEvent(OrderEvent event) throws JsonProcessingException {
        System.out.println("####: Successful order");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("order.successful", json); // For notification service
    }

    public void sendOrderCancelledEvent(OrderCancelledEvent event) throws JsonProcessingException { // For cancelling
        System.out.println("Cancelled order");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("order.cancelled", json);
    }

    public void sendOrderFailedEvent(String event) { // For failing
        System.out.println("Failed order");
        kafkaTemplate.send("order.failed", event);
    }

    public void sendOrderFailedEvent(OrderEvent event) throws JsonProcessingException {
        System.out.println("####: Failed order");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("order.successful", json); // For notification service
    }

    public void sendErrorEvent(ErrorEvent errorEvent) throws JsonProcessingException {
        System.out.println("####: Order not found");
        String json = mapper.writeValueAsString(errorEvent);
        kafkaTemplate.send("error", json);
    }

    public void sendPaymentFailedEvent(String message) {
        System.out.println("Failed paymemt");
        kafkaTemplate.send("payment.failed", message);
    }

    public void sendTicketCancelFailedEvent(String message) {
        System.out.println("Failed ticket cancel");
        kafkaTemplate.send("ticketCancel.failed", message);
    }
}
