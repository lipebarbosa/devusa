package com.devusa.identity.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected User() {
    }

    public User(UUID id, String name, String email, String passwordHash, Role role, Instant now) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null").toLowerCase().trim();
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash cannot be null");
        this.role = Objects.requireNonNullElse(role, Role.CLIENTE);
        this.createdAt = Objects.requireNonNullElse(now, Instant.now());
        this.updatedAt = this.createdAt;
    }

    public static User createPublicCustomer(String name, String email, String passwordHash) {
        return new User(UUID.randomUUID(), name, email, passwordHash, Role.CLIENTE, Instant.now());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateName(String newName) {
        this.name = Objects.requireNonNull(newName, "name cannot be null");
        this.updatedAt = Instant.now();
    }
}

