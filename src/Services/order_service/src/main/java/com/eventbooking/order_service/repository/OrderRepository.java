package com.eventbooking.order_service.repository;

import com.eventbooking.order_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.eventId = :eventId AND o.ticketId = :ticketId")
    Optional<Order> findExistingOrder(
            @Param("userId") String userId,
            @Param("eventId") String eventId,
            @Param("ticketId") String ticketId
    );

    List<Order> findByUserId(String userId);
}
