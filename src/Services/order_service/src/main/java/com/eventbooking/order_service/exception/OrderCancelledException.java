package com.eventbooking.order_service.exception;

public class OrderCancelledException extends RuntimeException{
    public OrderCancelledException(String message) {
        super(message);
    }
}
