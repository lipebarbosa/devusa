package com.devusa.identity.dto;

import java.time.Instant;
import java.util.UUID;

import com.devusa.identity.domain.Role;
import com.devusa.identity.domain.User;

public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}

