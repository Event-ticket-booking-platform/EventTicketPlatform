package com.eventticketbooking.ticket.ticket_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventticketbooking.ticket.ticket_service.entity.TicketEvent;

@Repository
public interface TicketEventRepository extends JpaRepository<TicketEvent, Long> {
}