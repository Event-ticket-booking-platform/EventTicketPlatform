package com.eventticketbooking.ticket.ticket_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReservedEvent {
    private String ticketId;
    private String eventId;
    private String seatNumber;
    private String userId;
    private int showNumber;
}
