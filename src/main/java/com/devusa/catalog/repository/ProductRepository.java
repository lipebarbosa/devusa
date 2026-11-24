package com.devusa.catalog.repository;

import java.util.List;
import java.util.UUID;

import com.devusa.catalog.domain.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @EntityGraph(attributePaths = {"category", "variants"})
    List<Product> findByActiveTrue();

    @EntityGraph(attributePaths = {"category", "variants"})
    List<Product> findByActiveTrueAndCategorySlug(String categorySlug);
}
