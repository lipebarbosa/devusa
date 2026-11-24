package com.devusa.catalog.controller;

import java.util.List;

import com.devusa.catalog.dto.ProductDto;
import com.devusa.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> listProducts(
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(catalogService.listProducts(category));
    }

    @PostMapping("/seed")
    public ResponseEntity<String> seedDemoData() {
        catalogService.seedDemoDataIfEmpty();
        return ResponseEntity.ok("Catálogo de demonstração populado com sucesso.");
    }
}
