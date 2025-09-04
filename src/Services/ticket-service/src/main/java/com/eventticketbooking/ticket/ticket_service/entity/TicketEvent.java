package com.eventticketbooking.ticket.ticket_service.entity;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "events")
public class TicketEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @Column(name = "start_utc", nullable = false)
    private Instant startUtc;

    @Column(name = "end_utc", nullable = false)
    private Instant endUtc;

    @Column(name = "organizer_id", nullable = false)
    private String organizerId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Instant getStartUtc() { return startUtc; }
    public void setStartUtc(Instant startUtc) { this.startUtc = startUtc; }

    public Instant getEndUtc() { return endUtc; }
    public void setEndUtc(Instant endUtc) { this.endUtc = endUtc; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
}
