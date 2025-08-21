package com.eventbooking.order_service.service;

import com.eventbooking.order_service.dto.*;
import com.eventbooking.order_service.exception.EventSerializationException;
import com.eventbooking.order_service.exception.OrderAlreadyExistsException;
import com.eventbooking.order_service.exception.OrderAlreadyPaidException;
import com.eventbooking.order_service.exception.OrderNotFoundException;
import com.eventbooking.order_service.model.Order;
import com.eventbooking.order_service.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final EventProducer eventProducer;

    @Transactional
    public Order createOrder(TicketReserved request, boolean throwOnDuplicate) throws JsonProcessingException, OrderAlreadyExistsException {
        Optional<Order> existingOrder = orderRepository
                .findExistingOrder(
                        request.getUserId(),
                        request.getEventId(),
                        request.getTicketId()
                );

        if(existingOrder.isPresent()) {
            System.out.println("####: Order already exists");
            if(throwOnDuplicate) {
                throw new OrderAlreadyExistsException("Order already exists for ticket " + request.getTicketId());
            } else {
                return existingOrder.get();
            }
        }

        Order order = Order.builder()
                .userId(request.getUserId())
                .eventId(request.getEventId())
                .ticketId(request.getTicketId())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        OrderEvent orderEvent = new OrderEvent(order.getId().toString(), order.getPrice(), order.getStatus());
        eventProducer.sendOrderCreatedEvent(orderEvent);

        System.out.println("####: Order " + order.getId() + " created.");
        return order;

    }

    public Order getById(Long orderId) {
        Optional<Order> existingOrder = orderRepository.findById(orderId);
        return existingOrder.orElse(null);
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public void handlePaymentProcessed(PaymentEvent event) throws JsonProcessingException {
        System.out.println("####: Handling Processed Payment");
        Optional<Order> orderOptional = orderRepository.findById(Long.parseLong(event.getOrderId()));

        if(orderOptional.isPresent()) {
            Order order = orderOptional.get();

            if(Objects.equals(event.getStatus(), "PAYMENT_COMPLETED")) {
                System.out.println("####: Payment Completed");
                order.setStatus("PAID");

                orderRepository.save(order);

                OrderEvent orderEvent = new OrderEvent(order.getId().toString(), order.getPrice(), order.getStatus());
                eventProducer.sendOrderSuccessfulEvent(orderEvent);

                System.out.println("####: Order " + order.getId() + " marked as PAID.");
            } else if(Objects.equals(event.getStatus(), "PAYMENT_FAILED")) {
                System.out.println("####: Payment Failed");
                order.setStatus("FAILED");

                orderRepository.save(order);

                OrderEvent orderEvent = new OrderEvent(order.getId().toString(), order.getPrice(), order.getStatus());
                eventProducer.sendOrderFailedEvent(orderEvent);

                System.out.println("####: Order " + order.getId() + " marked as FAILED.");
            }

        } else {
            ErrorEvent errorEvent = new ErrorEvent("OrderNotFound", "Order ID " + event.getOrderId() + " not found for payment processing.", LocalDateTime.now());
            eventProducer.sendErrorEvent(errorEvent);

            System.out.println("####: Order not found with Order ID: " + event.getOrderId());
        }
    }

    public void handleTicketExpired(TicketExpiredEvent event) throws JsonProcessingException {
        Optional<Order> orderOptional = orderRepository.findById(Long.parseLong(event.getOrderId()));

        if(orderOptional.isPresent()) {
            Order order = orderOptional.get();

            order.setStatus("CANCELLED");

            orderRepository.save(order);

            OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(order.getId().toString(), order.getUserId(), order.getTicketId(), event.getReason(), event.getExpirationTime());
            eventProducer.sendOrderCancelledEvent(orderCancelledEvent);

            System.out.println("Order " + order.getId() + " marked as CANCELED.");
        } else {
            ErrorEvent errorEvent = new ErrorEvent("OrderNotFound", "Order ID " + event.getOrderId() + " not found for payment processing.", LocalDateTime.now());
            eventProducer.sendErrorEvent(errorEvent);

            System.out.println("Order not found with Order ID: " + event.getOrderId());
        }
    }

    public Order handleOrderCancelled(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if ("PAID".equals(order.getStatus())) {
            throw new OrderAlreadyPaidException("Cannot cancel an order that is already paid.");
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
        OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent(order.getId().toString(), order.getUserId(), order.getTicketId(), "cancelled by user", LocalDateTime.now());

        try {
            eventProducer.sendOrderCancelledEvent(orderCancelledEvent);
        } catch(JsonProcessingException e) {
            throw new EventSerializationException("Invalid JSON" + e.getMessage());
        }

        System.out.println("Order " + order.getId() + " marked as CANCELED.");
        return order;
    }

    public Order handleCreateOrder(TicketReserved details) {
//                System.out.printf("####: Order Processed from TicketReserved details: " + order);
        try {
            return createOrder(details, true);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("Invalid order JSON: " + e.getOriginalMessage());
        }
    }

    public boolean isTicketReservedValid(TicketReserved event) {
        System.out.println("Validating");
        return event.getTicketId() != null && !event.getTicketId().isEmpty()
                && event.getUserId() != null && !event.getUserId().isEmpty()
                && event.getEventId() != null && !event.getEventId().isEmpty() // TODO: Validate all fields
                && event.getShowNumber() > 0;
    }


}
