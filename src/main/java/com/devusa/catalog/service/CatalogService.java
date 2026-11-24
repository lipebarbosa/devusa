package com.devusa.catalog.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.devusa.catalog.domain.Category;
import com.devusa.catalog.domain.Product;
import com.devusa.catalog.domain.ProductVariant;
import com.devusa.catalog.dto.ProductDto;
import com.devusa.catalog.repository.CategoryRepository;
import com.devusa.catalog.repository.ProductRepository;
import com.devusa.inventory.domain.InventoryItem;
import com.devusa.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public CatalogService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            InventoryRepository inventoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> listProducts(String categorySlug) {
        List<Product> products = (categorySlug == null || categorySlug.isBlank())
                ? productRepository.findByActiveTrue()
                : productRepository.findByActiveTrueAndCategorySlug(categorySlug.toLowerCase().trim());

        Map<UUID, Integer> stockMap = new HashMap<>();
        for (Product product : products) {
            for (ProductVariant variant : product.getVariants()) {
                inventoryRepository.findByVariantId(variant.getId())
                        .ifPresent(item -> stockMap.put(variant.getId(), item.getAvailableQuantity()));
            }
        }

        return products.stream()
                .map(p -> ProductDto.from(p, stockMap))
                .toList();
    }

    @Transactional
    public void seedDemoDataIfEmpty() {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category alfaiataria = categoryRepository.save(new Category(UUID.randomUUID(), "Alfaiataria Moderna", "alfaiataria"));
        Category outerwear = categoryRepository.save(new Category(UUID.randomUUID(), "Casacos & Outerwear", "outerwear"));
        Category essenciais = categoryRepository.save(new Category(UUID.randomUUID(), "Básicos & Seda", "essenciais"));

        createProductWithVariants(
                "Blazer Oversized Lã Fria",
                "Corte reto estruturado em lã fria italiana, ombros destacados e acabamento pespontado artesanal.",
                new BigDecimal("890.00"),
                alfaiataria,
                List.of(
                        new VariantSeed("BLZ-BLK-P", "P", "Preto Carvão", 5),
                        new VariantSeed("BLZ-BLK-M", "M", "Preto Carvão", 8),
                        new VariantSeed("BLZ-BLK-G", "G", "Preto Carvão", 4),
                        new VariantSeed("BLZ-BEI-M", "M", "Bege Areia", 6)
                )
        );

        createProductWithVariants(
                "Trench Coat Gabardine Impermeável",
                "Clássico atemporal revisitado com abotoamento duplo, cinto ajustável e forro acetinado contrastante.",
                new BigDecimal("1250.00"),
                outerwear,
                List.of(
                        new VariantSeed("TRC-KHK-M", "M", "Cáqui Militar", 7),
                        new VariantSeed("TRC-KHK-G", "G", "Cáqui Militar", 3),
                        new VariantSeed("TRC-BLK-M", "M", "Preto Noite", 5)
                )
        );

        createProductWithVariants(
                "Camisa Silk Touch Gola Padre",
                "Fluidez máxima em toque de seda maquinetada, toque suave e caimento minimalista sem bolsos aparentes.",
                new BigDecimal("420.00"),
                essenciais,
                List.of(
                        new VariantSeed("CMS-WHT-P", "P", "Branco Puro", 12),
                        new VariantSeed("CMS-WHT-M", "M", "Branco Puro", 15),
                        new VariantSeed("CMS-BLU-M", "M", "Azul Acinzentado", 6)
                )
        );

        createProductWithVariants(
                "Calça Pantalona Pregas Duplas",
                "Cintura alta com cós anatômico, pregas profundas e barra ampla com caimento escultural.",
                new BigDecimal("590.00"),
                alfaiataria,
                List.of(
                        new VariantSeed("CAL-GRY-38", "38", "Cinza Chumbo", 8),
                        new VariantSeed("CAL-GRY-40", "40", "Cinza Chumbo", 10),
                        new VariantSeed("CAL-BLK-40", "40", "Preto Carvão", 7)
                )
        );
    }

    private void createProductWithVariants(String name, String desc, BigDecimal price, Category cat, List<VariantSeed> variants) {
        Product product = new Product(UUID.randomUUID(), name, desc, price, cat);
        Product savedProduct = productRepository.save(product);

        for (VariantSeed vs : variants) {
            ProductVariant variant = new ProductVariant(UUID.randomUUID(), savedProduct, vs.sku, vs.size, vs.color, null);
            savedProduct.addVariant(variant);

            InventoryItem item = new InventoryItem(UUID.randomUUID(), variant.getId(), vs.stock);
            inventoryRepository.save(item);
        }

        productRepository.save(savedProduct);
    }

    private record VariantSeed(String sku, String size, String color, int stock) {}
}

