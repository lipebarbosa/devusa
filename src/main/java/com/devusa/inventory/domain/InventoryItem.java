package com.devusa.inventory.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "variant_id", nullable = false, unique = true)
    private UUID variantId;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int reservedQuantity;

    @Version
    private Long version;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected InventoryItem() {
    }

    public InventoryItem(UUID id, UUID variantId, int initialQuantity) {
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("Quantidade inicial não pode ser negativa");
        }
        this.id = Objects.requireNonNull(id);
        this.variantId = Objects.requireNonNull(variantId);
        this.totalQuantity = initialQuantity;
        this.reservedQuantity = 0;
        this.updatedAt = Instant.now();
    }

    public int getAvailableQuantity() {
        return totalQuantity - reservedQuantity;
    }

    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade a reservar deve ser positiva");
        }
        if (getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Estoque insuficiente para a variante: " + variantId);
        }
        this.reservedQuantity += quantity;
        this.updatedAt = Instant.now();
    }

    public void releaseReservation(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade a liberar deve ser positiva");
        }
        this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
        this.updatedAt = Instant.now();
    }

    public void commitReservation(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade a baixar deve ser positiva");
        }
        if (this.reservedQuantity < quantity || this.totalQuantity < quantity) {
            throw new IllegalStateException("Inconsistência de baixa de estoque");
        }
        this.reservedQuantity -= quantity;
        this.totalQuantity -= quantity;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getVariantId() {
        return variantId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public Long getVersion() {
        return version;
    }
}

