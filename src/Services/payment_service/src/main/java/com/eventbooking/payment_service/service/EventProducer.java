package com.eventbooking.payment_service.service;

import com.eventbooking.payment_service.dto.OrderCancelledEvent;
import com.eventbooking.payment_service.dto.PaymentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    public void sendPaymentProcessedEvent(PaymentDto event) throws JsonProcessingException {
        System.out.println("####: Sending message");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("payment.processed", json);
    }

    public void sendOrderErrorEvent(String message) {
        System.out.println("####: Invalid object");
        kafkaTemplate.send("order.error", message);
    }

    public void sendOrderCancelErrorEvent(String message) {
        System.out.println("####: Invalid object");
        kafkaTemplate.send("orderCancel.error", message);
    }

    public void sendOrderCancelFailedEvent(OrderCancelledEvent event) throws JsonProcessingException {
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("orderCancel.failed", json);
    }

    public void sendOrderCancelSuccessfulEvent(OrderCancelledEvent event) throws JsonProcessingException {
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("orderCancel.successful", json);
    }
}
