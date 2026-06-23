package com.udit.order_service.controller;

import com.udit.order_service.entity.Order;
import com.udit.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafka;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order placeOrder(@RequestBody Order order){
        Order newlyCreatedOrder = orderRepository.save(order);
        sendNotification(order);
        return newlyCreatedOrder;
    }

    private void sendNotification(Order order){
        kafka.send("order-created", order.getSkuCode());
    }
}
