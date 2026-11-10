package com.devusa.catalog.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(length = 30)
    private String size;

    @Column(length = 30)
    private String color;

    @Column(precision = 12, scale = 2)
    private BigDecimal priceOverride;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ProductVariant() {
    }

    public ProductVariant(UUID id, Product product, String sku, String size, String color, BigDecimal priceOverride) {
        this.id = Objects.requireNonNull(id);
        this.product = Objects.requireNonNull(product);
        this.sku = Objects.requireNonNull(sku).toUpperCase().trim();
        this.size = size;
        this.color = color;
        this.priceOverride = priceOverride;
        this.createdAt = Instant.now();
    }

    public BigDecimal getEffectivePrice() {
        return priceOverride != null ? priceOverride : product.getBasePrice();
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getSku() {
        return sku;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public BigDecimal getPriceOverride() {
        return priceOverride;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

