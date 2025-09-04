package com.eventticketbooking.ticket.ticket_service.repository;

import com.eventticketbooking.ticket.ticket_service.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowId(Long showId);
}
