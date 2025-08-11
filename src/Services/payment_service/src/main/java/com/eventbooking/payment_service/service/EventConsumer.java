package com.eventbooking.payment_service.service;

import com.eventbooking.payment_service.dto.OrderEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {
    private final ObjectMapper mapper;
    private final PaymentService paymentService;

    public EventConsumer(ObjectMapper mapper, PaymentService paymentService) {
        this.mapper = mapper;
        this.paymentService = paymentService;
    }

//    @KafkaListener(topics = "order.created", groupId = "orders")
//    public void listenToTopic(String receivedMessage) {
//
//        System.out.println("Message: " + receivedMessage);
//    }

//    @KafkaListener(topics = "orderEvent.created", groupId = "orders")
//    public void listenToEventTopic(OrderEvent receivedEvent) {
//
//        System.out.println("Event: " + receivedEvent.toString());
//    }

    @KafkaListener(topics = "order.created", groupId = "payment-service")
    public void handleOrderCreated(String message) throws JsonProcessingException {
        try {
            OrderEvent event = mapper.readValue(message, OrderEvent.class);
            System.out.println("####: Order Created Event: " + message);

            if(isValid(event)) {
                paymentService.createPayment(event);
                System.out.println("####: Payment Processed from Order event: " + event);
            } else {
                System.out.println("####: Invalid data: " + event); // TODO: Should we publish an event?
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isValid(OrderEvent event) {
        return event.getOrderId() != null && !event.getOrderId().isEmpty()
                && event.getAmount() != null
                && event.getStatus() != null && !event.getStatus().isEmpty(); // TODO: Validate all fields
    }


}
