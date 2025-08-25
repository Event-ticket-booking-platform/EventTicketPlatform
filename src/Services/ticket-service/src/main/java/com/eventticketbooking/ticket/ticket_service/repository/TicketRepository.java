package com.eventticketbooking.ticket.ticket_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventticketbooking.ticket.ticket_service.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {

    List<Ticket> findByEventId(Long eventId);

    Optional<Ticket> findByEventIdAndSeatNumber(Long eventId, String seatNumber);

    List<Ticket> findByEventIdAndReservedFalse(Long eventId);

    // Needed for confirm/release
    List<Ticket> findByOrderId(String orderId);

    // Needed for expiration check
    @Query("SELECT t FROM Ticket t WHERE t.reserved = true AND t.confirmed = false AND t.reservedAt < :expiryTime")
    List<Ticket> findExpiredReservations(@Param("expiryTime") LocalDateTime expiryTime);
    
}