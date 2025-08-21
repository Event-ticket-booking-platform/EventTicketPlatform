package com.eventbooking.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketExpiredEvent {
    private String ticketId;
    private String orderId;
    private String userId;
    private LocalDateTime expirationTime;
    private String reason;
}

