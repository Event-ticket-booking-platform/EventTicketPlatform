package com.eventticketbooking.ticket.ticket_service.repository;

import com.eventticketbooking.ticket.ticket_service.entity.EventShow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventShowRepository extends JpaRepository<EventShow, Long> {
    List<EventShow> findByEventId(Long eventId);
}
