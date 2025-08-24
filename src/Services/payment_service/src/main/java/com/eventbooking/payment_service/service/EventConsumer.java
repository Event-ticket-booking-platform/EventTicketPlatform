package com.eventbooking.payment_service.service;

import com.eventbooking.payment_service.dto.OrderCancelledEvent;
import com.eventbooking.payment_service.dto.OrderEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {
    private final ObjectMapper mapper;
    private final PaymentService paymentService;
    private final EventProducer eventProducer;

    public EventConsumer(ObjectMapper mapper, PaymentService paymentService, EventProducer eventProducer) {
        this.mapper = mapper;
        this.paymentService = paymentService;
        this.eventProducer = eventProducer;
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
                System.out.println("####: Invalid data: " + event);
                eventProducer.sendOrderErrorEvent(message);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            eventProducer.sendOrderErrorEvent(message);

        }

    }

    @KafkaListener(topics = "order.cancelled", groupId = "payment-service")
    public void handleOrderCancelled(String message) throws JsonProcessingException {
        try {
            OrderCancelledEvent event = mapper.readValue(message, OrderCancelledEvent.class);
            System.out.println("####: Order Created Event: " + message);

            if(isOrderCancelledValid(event)) {
                paymentService.cancelPayment(event);
                System.out.println("####: Payment Processed from Order event: " + event);
            } else {
                System.out.println("####: Invalid data: " + event);
                eventProducer.sendOrderCancelErrorEvent(message);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            eventProducer.sendOrderCancelErrorEvent(message);

        }

    }

    private boolean isValid(OrderEvent event) {
        return event.getOrderId() != null && !event.getOrderId().isEmpty()
                && event.getAmount() != null
                && event.getStatus() != null && !event.getStatus().isEmpty(); // TODO: Validate all fields
    }

    private boolean isOrderCancelledValid(OrderCancelledEvent event) {
        return event.getOrderId() != null && !event.getOrderId().isEmpty()
                && event.getUserId() != null && !event.getUserId().isEmpty()
                && event.getTicketId() != null && !event.getTicketId().isEmpty()
                && event.getCancelReason() != null && !event.getCancelReason().isEmpty()
                && event.getTimestamp() != null
                ;
    }
}
