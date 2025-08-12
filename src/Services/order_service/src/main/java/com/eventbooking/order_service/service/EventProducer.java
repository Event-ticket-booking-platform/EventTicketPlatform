package com.eventbooking.order_service.service;

//import com.eventbooking.order_service.dto.OrderEvent;
//import lombok.RequiredArgsConstructor;
import com.eventbooking.order_service.dto.ErrorEvent;
import com.eventbooking.order_service.dto.OrderCancelledEvent;
import com.eventbooking.order_service.dto.OrderEvent;
import com.eventbooking.order_service.dto.TicketExpiredEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
//@RequiredArgsConstructor
public class EventProducer {

    //    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

//    @Autowired
//    KafkaTemplate<String, OrderEvent> eventKafkaTemplate;

//    public void sendOrderEvent(OrderEvent event) {
//        kafkaTemplate.send("order.created", event);
//    }

//    public void sendOrderMessage(String message) {
//        kafkaTemplate.send("order.created", message);
//    }

//    public void sendOrderEvent(OrderEvent event) throws JsonProcessingException {
//        System.out.println("Sending order");
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(event);
//        eventKafkaTemplate.send("orderEvent.created", event);
//    }

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

    public void sendOrderCancelledEvent(OrderCancelledEvent event) throws JsonProcessingException {
        System.out.println("Cancelled order");
        String json = mapper.writeValueAsString(event);
        kafkaTemplate.send("order.cancelled", json);
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
//
//    public void sendPaymentRequestedEvent(PaymentRequestedEvent event) throws JsonProcessingException {
//        String json = mapper.writeValueAsString(event);
//        kafkaTemplate.send("payment.requested", json);
//    }
//
//    public void sendTicketReleasedEvent(TicketReleasedEvent event) throws JsonProcessingException {
//        String json = mapper.writeValueAsString(event);
//        kafkaTemplate.send("ticket.released", json);
//    }
}
