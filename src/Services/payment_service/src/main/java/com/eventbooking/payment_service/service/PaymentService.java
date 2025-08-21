package com.eventbooking.payment_service.service;

import com.eventbooking.payment_service.dto.OrderEvent;
import com.eventbooking.payment_service.dto.PaymentDto;
import com.eventbooking.payment_service.exception.PaymentNotFoundException;
import com.eventbooking.payment_service.model.Payment;
import com.eventbooking.payment_service.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final EventProducer eventProducer;

    @Transactional
    public Payment createPayment(OrderEvent event) throws JsonProcessingException {
        Optional<Payment> existingPayment = paymentRepository
                .findByOrderId(
                        event.getOrderId()
                );

        if(existingPayment.isPresent()) {
            System.out.println("Payment already exists for order: " + event.getOrderId());
            return existingPayment.get();
        }
        System.out.println("####: Creating payment");
        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .amount(event.getAmount())
                .timestamp(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        boolean isPaid = processPayment(payment);

        if(isPaid){
            payment.setStatus("PAYMENT_COMPLETED");
        } else {
            payment.setStatus("PAYMENT_FAILED");
        }

        PaymentDto paymentEvent =new PaymentDto(payment.getId(), payment.getOrderId(), payment.getStatus(), payment.getAmount());

        eventProducer.sendPaymentProcessedEvent(paymentEvent);

        return payment;
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
}
