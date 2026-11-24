package com.devusa.catalog.repository;

import java.util.Optional;
import java.util.UUID;

import com.devusa.catalog.domain.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    Optional<ProductVariant> findBySku(String sku);
}

