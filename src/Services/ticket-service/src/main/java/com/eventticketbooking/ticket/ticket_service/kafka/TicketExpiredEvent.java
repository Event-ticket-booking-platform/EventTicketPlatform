package com.eventticketbooking.ticket.ticket_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketExpiredEvent {
    private String ticketId;
    private String orderId;
    private String userId;
    private LocalDateTime expirationTime;
    private String reason;
}