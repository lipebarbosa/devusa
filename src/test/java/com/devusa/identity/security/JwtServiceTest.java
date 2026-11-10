package com.devusa.identity.security;

import java.time.Instant;
import java.util.UUID;

import com.devusa.identity.domain.Role;
import com.devusa.identity.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("verySecretSigningKeyForDevUsaPlatform1234567890", 900L);
    }

    @Test
    void shouldGenerateAndValidateTokenSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "Maria Silva", "maria@test.com", "hash", Role.GERENTE, Instant.now());

        String token = jwtService.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtService.extractEmail(token)).isEqualTo("maria@test.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("GERENTE");
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        assertThat(jwtService.isTokenValid("invalid.jwt.token")).isFalse();
    }
}

