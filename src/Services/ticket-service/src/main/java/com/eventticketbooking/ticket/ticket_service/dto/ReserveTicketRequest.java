package com.eventticketbooking.ticket.ticket_service.dto;

import lombok.Data;

@Data
public class ReserveTicketRequest {
    private Long eventId;
    private String seatNumber;  
    private Long userId;

    public Long getEventId() {
        return eventId;
    }
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
    
}
