package com.eventticketbooking.ticket.ticket_service.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_shows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventShow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private TicketEvent event;

    @Column(name = "show_number", nullable = false)
    private int showNumber;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
}

