package com.eventticketbooking.ticket.ticket_service.kafka;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReservedEvent { 
    private List<Long> ticketIds;
    private String eventId;
    private String userId;
    private Double ticketPrice;
    private Integer seatCount;
}