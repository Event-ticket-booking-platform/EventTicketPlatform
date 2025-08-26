package com.eventbooking.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelledEvent {
    private String orderId;
    private String userId;
    private String ticketId;
    private String cancelReason;  // e.g., "USER_CANCELLED" or "TICKET_EXPIRED"
    private LocalDateTime timestamp;     // ISO 8601 format or LocalDateTime string
}
