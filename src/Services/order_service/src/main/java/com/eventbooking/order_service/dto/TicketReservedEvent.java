package com.eventbooking.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketReservedEvent {
    private String ticketId;
    private String userId;
    private String eventId;
    private int quantity;
    private double price;
    private int showNumber;
}
