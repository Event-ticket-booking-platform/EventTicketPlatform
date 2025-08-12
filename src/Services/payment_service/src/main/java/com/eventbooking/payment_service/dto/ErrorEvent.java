package com.eventbooking.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorEvent {
    private String errorType;
    private String errorMessage;
    private LocalDateTime timestamp;
}
