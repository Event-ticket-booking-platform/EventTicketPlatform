package com.eventbooking.order_service.controller;

//import com.eventbooking.order_service.dto.OrderEvent;
//import com.eventbooking.order_service.dto.OrderRequest;
//import com.eventbooking.order_service.model.Order;
//import com.eventbooking.order_service.service.OrderEventProducer;
//import com.eventbooking.order_service.service.OrderService;
import com.eventbooking.order_service.dto.OrderEvent;
import com.eventbooking.order_service.service.EventConsumer;
import com.eventbooking.order_service.service.EventProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
//    private final OrderService orderService;
    private final EventConsumer eventConsumer;

//    @PostMapping
//    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request){
//        Order order = orderService.createOrder(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(order);
//    }
//
//    @GetMapping
//    public List<Order> getAll(){
//        return orderService.getAll();
//    }
//
//    @GetMapping("/{id}")
//    public Order getById(@PathVariable Long id){
//        return orderService.getById(id);
//    }
//
//    @PostMapping("/order")
//    public ResponseEntity<String> placeOrder(@RequestBody OrderEvent orderEvent) {
//        producer.sendOrderEvent(orderEvent);
//        return ResponseEntity.ok("Order Event Sent to Kafka");
//    }

//    @GetMapping("/createOrder")
//    public void getMessageFromClient(@RequestParam("message") String message) {
//        orderEventProducer.sendOrderMessage(message);
//    }

    @PostMapping("/createOrderEvent")
    public void getEventFromClient(@RequestBody String message) throws JsonProcessingException {
        System.out.println("####: Order received");
        eventConsumer.handleTicketReserved(message);
    }
}
