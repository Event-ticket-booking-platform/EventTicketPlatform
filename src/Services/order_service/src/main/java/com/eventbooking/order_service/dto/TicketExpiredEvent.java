package com.eventbooking.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketExpiredEvent {
    private String ticketId;
    private String orderId;
    private String userId;
    private String expirationTime;
    private String reason;
}

