package com.devusa.catalog.repository;

import java.util.Optional;
import java.util.UUID;

import com.devusa.catalog.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
}

