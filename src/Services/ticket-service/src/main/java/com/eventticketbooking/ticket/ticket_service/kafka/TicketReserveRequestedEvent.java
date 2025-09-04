package com.eventticketbooking.ticket.ticket_service.kafka;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReserveRequestedEvent {
    private String orderId;
    private Long eventId;
    private Long showId;
    private List<String> seatNumbers;
    private String userId;
}