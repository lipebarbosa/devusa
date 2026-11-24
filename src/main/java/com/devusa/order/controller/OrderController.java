package com.devusa.order.controller;

import java.util.List;
import java.util.UUID;

import com.devusa.order.dto.OrderDtos;
import com.devusa.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDtos.OrderResponse> checkout(
            @Valid @RequestBody OrderDtos.CheckoutRequest request
    ) {
        OrderDtos.OrderResponse response = orderService.checkout(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/transition")
    public ResponseEntity<OrderDtos.OrderResponse> transition(
            @PathVariable UUID id,
            @RequestBody OrderDtos.TransitionRequest request
    ) {
        return ResponseEntity.ok(orderService.advanceStatus(id, request.nextStatus()));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<OrderDtos.OrderResponse>> getRecentOrders() {
        return ResponseEntity.ok(orderService.listRecentOrders());
    }
}

