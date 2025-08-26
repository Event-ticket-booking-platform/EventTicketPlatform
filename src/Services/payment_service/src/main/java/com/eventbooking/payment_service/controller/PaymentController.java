package com.eventbooking.payment_service.controller;

import com.eventbooking.payment_service.dto.PaymentDto;
import com.eventbooking.payment_service.model.Payment;
import com.eventbooking.payment_service.service.EventConsumer;
import com.eventbooking.payment_service.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final EventConsumer eventConsumer;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPaymentInfoById(@PathVariable Long paymentId) {
        PaymentDto payment = paymentService.getPaymentInfoById(paymentId);
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PaymentDto> getPaymentInfoByOrderId(@PathVariable String orderId) {
        PaymentDto payment = paymentService.getPaymentInfoByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(payment);
    }

    //TESTING
//    @PostMapping("/orderCancelling")
//    public ResponseEntity orderCancelling(@RequestBody String request) throws JsonProcessingException {
//        eventConsumer.handleOrderCancelled(request);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }
}
