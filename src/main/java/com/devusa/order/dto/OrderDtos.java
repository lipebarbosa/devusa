package com.devusa.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.devusa.order.domain.Order;
import com.devusa.order.domain.OrderStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class OrderDtos {

    public record CheckoutRequest(
            @NotBlank String customerName,
            @NotBlank @Email String customerEmail,
            String idempotencyKey,
            @NotEmpty List<CheckoutItemRequest> items
    ) {}

    public record CheckoutItemRequest(
            UUID variantId,
            int quantity
    ) {}

    public record OrderResponse(
            UUID id,
            String customerName,
            String customerEmail,
            OrderStatus status,
            BigDecimal totalAmount,
            Instant createdAt,
            List<OrderItemResponse> items
    ) {
        public static OrderResponse from(Order order) {
            return new OrderResponse(
                    order.getId(),
                    order.getCustomerName(),
                    order.getCustomerEmail(),
                    order.getStatus(),
                    order.getTotalAmount(),
                    order.getCreatedAt(),
                    order.getItems().stream()
                            .map(i -> new OrderItemResponse(
                                    i.getVariantId(),
                                    i.getProductName(),
                                    i.getSku(),
                                    i.getUnitPrice(),
                                    i.getQuantity()
                            ))
                            .toList()
            );
        }
    }

    public record OrderItemResponse(
            UUID variantId,
            String productName,
            String sku,
            BigDecimal unitPrice,
            int quantity
    ) {}

    public record TransitionRequest(
            OrderStatus nextStatus
    ) {}
}
