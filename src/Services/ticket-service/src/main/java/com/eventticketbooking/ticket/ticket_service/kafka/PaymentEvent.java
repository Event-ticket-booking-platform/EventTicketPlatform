package com.eventticketbooking.ticket.ticket_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private String paymentId;
    private String orderId;
    private String status; // e.g., SUCCESS, FAILED
    private Double amount;
}