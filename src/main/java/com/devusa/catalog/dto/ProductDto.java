package com.devusa.catalog.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.devusa.catalog.domain.Product;
import com.devusa.catalog.domain.ProductVariant;

public record ProductDto(
        UUID id,
        String name,
        String description,
        BigDecimal basePrice,
        String categoryName,
        String categorySlug,
        List<VariantDto> variants
) {
    public static ProductDto from(Product product, java.util.Map<UUID, Integer> stockMap) {
        List<VariantDto> variantDtos = product.getVariants().stream()
                .map(v -> new VariantDto(
                        v.getId(),
                        v.getSku(),
                        v.getSize(),
                        v.getColor(),
                        v.getEffectivePrice(),
                        stockMap.getOrDefault(v.getId(), 0)
                ))
                .toList();

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                product.getCategory() != null ? product.getCategory().getName() : "Geral",
                product.getCategory() != null ? product.getCategory().getSlug() : "geral",
                variantDtos
        );
    }

    public record VariantDto(
            UUID id,
            String sku,
            String size,
            String color,
            BigDecimal price,
            int availableStock
    ) {
    }
}
