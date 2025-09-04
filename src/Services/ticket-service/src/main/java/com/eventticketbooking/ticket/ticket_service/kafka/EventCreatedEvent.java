package com.eventticketbooking.ticket.ticket_service.kafka;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreatedEvent {
    private String title;
    private String description;
    private String location;
    private Instant startUtc;
    private Instant endUtc;
    private String organizerId;
}
