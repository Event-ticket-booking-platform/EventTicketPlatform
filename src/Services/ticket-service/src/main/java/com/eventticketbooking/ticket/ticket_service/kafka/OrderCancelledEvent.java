package com.eventticketbooking.ticket.ticket_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCancelledEvent {
    private String orderId;
    private String userId;
    private String ticketId;
    private String cancelReason;  
    private LocalDateTime timestamp;
}