package com.eventbooking.order_service.exception;

public class OrderAlreadyPaidException extends RuntimeException{
    public OrderAlreadyPaidException(String message) {
        super(message);
    }
}
