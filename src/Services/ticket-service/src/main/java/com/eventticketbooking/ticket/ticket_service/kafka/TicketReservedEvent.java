package com.eventticketbooking.ticket.ticket_service.kafka;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReservedEvent { 
    @NotEmpty private String ticketId;
    @NotEmpty private String userId;
    @NotEmpty private String eventId;
    @Positive private int quantity;
    @Positive private double price;
    @Positive private int showNumber;
}