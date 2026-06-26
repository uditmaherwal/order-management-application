package com.udit.order_service.controller;

import com.udit.order_service.entity.Order;
import com.udit.order_service.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafka;
    private final WebClient.Builder webClientBuilder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "fallbackInventory")
    public Order placeOrder(@RequestBody Order order){
        boolean inStock = Boolean.TRUE.equals(webClientBuilder
                .build()
                .get()
                .uri("http://localhost:8082/api/inventory/{skuCode}?quantity={quantity}", order.getSkuCode(), order.getQuantity())
                .retrieve().bodyToMono(Boolean.class).block());
        if(inStock){
            webClientBuilder
                    .build()
                    .patch()
                    .uri("http://localhost:8082/api/inventory/{skuCode}?quantity={quantity}", order.getSkuCode(), order.getQuantity())
                    .retrieve().bodyToMono(String.class).block();
            Order newlyCreatedOrder = orderRepository.save(order);
            sendNotification(order);
            return newlyCreatedOrder;
        }else{
            throw new IllegalArgumentException("Product is not in stock");
        }
    }

    public Order fallbackInventory(Order order, Throwable throwable) {
        throw new RuntimeException(
                "We are currently having trouble reaching our inventory system",
                throwable
        );
    }

    private void sendNotification(Order order){
        kafka.send("order-created", order.getSkuCode());
    }
}
