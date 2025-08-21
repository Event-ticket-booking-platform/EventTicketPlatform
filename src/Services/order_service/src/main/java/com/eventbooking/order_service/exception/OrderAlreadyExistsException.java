package com.eventbooking.order_service.exception;

public class OrderAlreadyExistsException extends RuntimeException{
    public OrderAlreadyExistsException(String message) {
        super(message);
    }
}
