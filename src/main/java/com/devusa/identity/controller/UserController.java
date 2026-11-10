package com.devusa.identity.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENTE', 'GERENTE', 'ESTOQUE', 'EXPEDICAO')")
    public ResponseEntity<Map<String, String>> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(Map.of("userId", principal.getName()));
    }

    @GetMapping("/manager-only")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Map<String, String>> getManagerDashboard() {
        return ResponseEntity.ok(Map.of("message", "Área restrita de gestão"));
    }
}

