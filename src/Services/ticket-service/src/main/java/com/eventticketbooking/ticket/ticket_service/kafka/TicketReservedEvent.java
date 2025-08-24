package com.eventticketbooking.ticket.ticket_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReservedEvent {
    private String ticketId;
    private String userId;
    private String eventId;
    private int quantity;
    private double price;
    private int showNumber;
}
