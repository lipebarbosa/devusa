package com.devusa.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.devusa.catalog.domain.ProductVariant;
import com.devusa.catalog.repository.ProductVariantRepository;
import com.devusa.inventory.domain.InventoryItem;
import com.devusa.inventory.repository.InventoryRepository;
import com.devusa.order.domain.Order;
import com.devusa.order.domain.OrderItem;
import com.devusa.order.domain.OrderStatus;
import com.devusa.order.dto.OrderDtos;
import com.devusa.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductVariantRepository variantRepository;
    private final InventoryRepository inventoryRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductVariantRepository variantRepository,
            InventoryRepository inventoryRepository
    ) {
        this.orderRepository = orderRepository;
        this.variantRepository = variantRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public OrderDtos.OrderResponse checkout(OrderDtos.CheckoutRequest request, UUID userId) {
        // Idempotência
        if (request.idempotencyKey() != null && !request.idempotencyKey().isBlank()) {
            Optional<Order> existing = orderRepository.findByIdempotencyKey(request.idempotencyKey().trim());
            if (existing.isPresent()) {
                return OrderDtos.OrderResponse.from(existing.get());
            }
        }

        BigDecimal total = BigDecimal.ZERO;
        Order order = new Order(
                UUID.randomUUID(),
                userId,
                request.customerName().trim(),
                request.customerEmail().trim().toLowerCase(),
                BigDecimal.ZERO,
                request.idempotencyKey() != null ? request.idempotencyKey().trim() : null
        );

        for (OrderDtos.CheckoutItemRequest itemReq : request.items()) {
            if (itemReq.quantity() <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
            }

            ProductVariant variant = variantRepository.findById(itemReq.variantId())
                    .orElseThrow(() -> new IllegalArgumentException("Variante não encontrada: " + itemReq.variantId()));

            // Reserva de estoque atômica com lock pessimista
            InventoryItem inventory = inventoryRepository.findByVariantIdForUpdate(variant.getId())
                    .orElseThrow(() -> new IllegalStateException("Item de estoque não cadastrado"));

            inventory.reserve(itemReq.quantity());
            inventoryRepository.save(inventory);

            BigDecimal unitPrice = variant.getEffectivePrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.quantity()));
            total = total.add(subtotal);

            OrderItem orderItem = new OrderItem(
                    UUID.randomUUID(),
                    order,
                    variant.getId(),
                    variant.getProduct().getName(),
                    variant.getSku(),
                    unitPrice,
                    itemReq.quantity()
            );
            order.addItem(orderItem);
        }

        // Definir total definitivo calculado pelo backend
        order = orderRepository.save(new Order(
                order.getId(),
                order.getUserId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                total,
                order.getIdempotencyKey()
        ));
        for (OrderItem item : order.getItems()) {
            order.addItem(item);
        }

        return OrderDtos.OrderResponse.from(order);
    }

    @Transactional
    public OrderDtos.OrderResponse advanceStatus(UUID orderId, OrderStatus nextStatus) {
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        order.transitionTo(nextStatus);

        // Se foi pago, efetiva a baixa do estoque reservado
        if (nextStatus == OrderStatus.PAGO) {
            for (OrderItem item : order.getItems()) {
                InventoryItem inventory = inventoryRepository.findByVariantIdForUpdate(item.getVariantId())
                        .orElseThrow();
                inventory.commitReservation(item.getQuantity());
                inventoryRepository.save(inventory);
            }
        }

        // Se foi cancelado ou expirado, libera a reserva
        if (nextStatus == OrderStatus.CANCELADO || nextStatus == OrderStatus.EXPIRADO) {
            for (OrderItem item : order.getItems()) {
                InventoryItem inventory = inventoryRepository.findByVariantIdForUpdate(item.getVariantId())
                        .orElseThrow();
                inventory.releaseReservation(item.getQuantity());
                inventoryRepository.save(inventory);
            }
        }

        Order saved = orderRepository.save(order);
        return OrderDtos.OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDtos.OrderResponse> listRecentOrders() {
        return orderRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(OrderDtos.OrderResponse::from)
                .toList();
    }
}

