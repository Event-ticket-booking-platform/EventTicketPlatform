package com.eventticketbooking.ticket.ticket_service.kafka;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketReserveRequestedEvent {
    private String orderId;

    // Event details
    private String eventId;          // keep it as String if it comes that way from Kafka
    private String title;
    private String location;
    private Instant startUtc;
    private Instant endUtc;
    private String organizerId;
    private Double ticketPrice;
    private Instant createdUtc;

    // Reservation details
    private Integer requestedSeats;  // how many seats the user wants
    private String userId;

}