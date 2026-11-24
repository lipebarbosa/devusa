package com.devusa.order.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.devusa.order.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByIdempotencyKey(String idempotencyKey);

    @EntityGraph(attributePaths = {"items"})
    Optional<Order> findWithItemsById(UUID id);

    @EntityGraph(attributePaths = {"items"})
    List<Order> findTop10ByOrderByCreatedAtDesc();
}

