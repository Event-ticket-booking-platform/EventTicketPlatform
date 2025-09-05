package com.eventticketbooking.ticket.ticket_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReservedEvent { 
    private Long ticketId;  
    private String eventId;
    private String userId;
    private Double ticketPrice;
    private Integer seatCount;
}