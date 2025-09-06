package com.eventticketbooking.ticket.ticket_service.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
public class TicketEvent {
    @Id
    @Column(name = "id")
    @JsonProperty("EventId") 
    private String eventId;

    @Column(nullable = false)
    @JsonProperty("Title")
    private String title;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("Description")
    private String description;

    @Column(name = "location")
    @JsonProperty("Location")
    private String location;

    @Column(name = "start_utc", nullable = false)
    @JsonProperty("StartUtc")
    private Instant startUtc;

    @Column(name = "end_utc", nullable = false)
    @JsonProperty("EndUtc")
    private Instant endUtc;

    @Column(name = "organizer_id", nullable = false)
    @JsonProperty("OrganizerId")
    private String organizerId;

    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats=0;
}
