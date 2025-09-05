package com.eventticketbooking.ticket.ticket_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventticketbooking.ticket.ticket_service.entity.Ticket;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {

     List<Ticket> findByEventId(Long eventId);

    Optional<Ticket> findByEventIdAndSeatNumber(Long eventId, Integer seatNumber);

    List<Ticket> findByEventIdAndReservedFalse(Long eventId);

    List<Ticket> findByOrderId(String orderId);

    @Query("SELECT t FROM Ticket t WHERE t.reserved = true AND t.confirmed = false AND t.reservedAt < :expiryTime")
    List<Ticket> findExpiredReservations(@Param("expiryTime") LocalDateTime expiryTime);

    // Count confirmed tickets (sold tickets)
    long countByConfirmedTrue();

    // Count sold tickets for a specific event
    long countByEventIdAndConfirmedTrue(Long eventId);
    
}