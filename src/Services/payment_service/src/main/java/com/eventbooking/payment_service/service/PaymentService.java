package com.eventbooking.payment_service.service;

import com.eventbooking.payment_service.dto.OrderCancelledEvent;
import com.eventbooking.payment_service.dto.OrderEvent;
import com.eventbooking.payment_service.dto.PaymentDto;
import com.eventbooking.payment_service.exception.PaymentNotFoundException;
import com.eventbooking.payment_service.model.Payment;
import com.eventbooking.payment_service.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final EventProducer eventProducer;

    @Transactional
    public void createPayment(OrderEvent event) throws JsonProcessingException {
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(event.getOrderId());
        Payment payment;

        if(existingPayment.isPresent()) {
            System.out.println("Payment already exists for order: " + event.getOrderId());
            payment = existingPayment.get();
            if(Objects.equals(payment.getStatus(), "PAYMENT_COMPLETED")) {
                ObjectMapper mapper = new ObjectMapper();
                eventProducer.sendOrderErrorEvent(mapper.writeValueAsString(event));
                System.out.println("Already paid for order.");
            }
//            return existingPayment.get();
        } else {
            System.out.println("####: Creating payment");
            payment = Payment.builder()
                    .orderId(event.getOrderId())
                    .amount(event.getAmount())
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        boolean isPaid = processPayment(payment);

        if(isPaid){
            payment.setStatus("PAYMENT_COMPLETED");
            PaymentDto paymentEvent =new PaymentDto(payment.getId(), payment.getOrderId(), payment.getStatus(), payment.getAmount());
            eventProducer.sendPaymentProcessedEvent(paymentEvent);
        } else {
            payment.setStatus("PAYMENT_FAILED");
            PaymentDto paymentEvent =new PaymentDto(payment.getId(), payment.getOrderId(), payment.getStatus(), payment.getAmount());
            eventProducer.sendPaymentProcessedEvent(paymentEvent);
        }
        paymentRepository.save(payment);

//        PaymentDto paymentEvent =new PaymentDto(payment.getId(), payment.getOrderId(), payment.getStatus(), payment.getAmount());
//
//        eventProducer.sendPaymentProcessedEvent(paymentEvent);
    }

    private boolean processPayment(Payment payment) {
        System.out.println("####: Processing payment");
        // SIMULATION ONLY
        return new Random().nextBoolean();
    }

    public PaymentDto getPaymentInfoById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));

        return new PaymentDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getStatus(),
                payment.getAmount()
        );
    }

    public PaymentDto getPaymentInfoByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException("Payment not found with Order ID: " + orderId));

        return new PaymentDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getStatus(),
                payment.getAmount()
        );
    }

    public void cancelPayment(OrderCancelledEvent event) throws JsonProcessingException {
        Optional<Payment> paymentOptional = paymentRepository.findPaymentByPrderId(event.getOrderId());
        if(paymentOptional.isPresent()) {
            if(Objects.equals(paymentOptional.get().getStatus(), "PAID")) {
                System.out.println("Already Paid for order.");
                eventProducer.sendOrderCancelFailedEvent(event);
            } else {
                paymentOptional.get().setStatus("CANCELLED");
                paymentRepository.save(paymentOptional.get());
                eventProducer.sendOrderCancelSuccessfulEvent(event);
            }
        } else {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(event);
            eventProducer.sendOrderCancelErrorEvent(json);
        }
    }
}
