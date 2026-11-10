package com.devusa.identity.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import com.devusa.identity.domain.RefreshToken;
import com.devusa.identity.domain.User;
import com.devusa.identity.dto.AuthResponse;
import com.devusa.identity.dto.LoginRequest;
import com.devusa.identity.dto.RefreshTokenRequest;
import com.devusa.identity.dto.RegisterRequest;
import com.devusa.identity.dto.UserResponse;
import com.devusa.identity.repository.RefreshTokenRepository;
import com.devusa.identity.repository.UserRepository;
import com.devusa.identity.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long refreshTokenExpirationSeconds;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${security.jwt.refresh-expiration-seconds:604800}") long refreshTokenExpirationSeconds // 7 dias
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenExpirationSeconds = refreshTokenExpirationSeconds;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("O e-mail informado já está em uso.");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        // Regra do README: cadastro público sempre define papel como CLIENTE
        User user = User.createPublicCustomer(request.name().trim(), normalizedEmail, passwordHash);
        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String rawRefreshToken = createAndSaveRefreshToken(user);

        return AuthResponse.of(accessToken, rawRefreshToken, jwtService.getAccessTokenExpirationSeconds(), UserResponse.from(user));
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String rawToken = request.refreshToken();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Refresh token inválido ou revogado."));

        if (!refreshToken.isValid()) {
            throw new BadCredentialsException("Refresh token expirado ou revogado.");
        }

        // Rotação de token: revoga o token anterior para evitar replay attacks
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRawRefreshToken = createAndSaveRefreshToken(user);

        return AuthResponse.of(newAccessToken, newRawRefreshToken, jwtService.getAccessTokenExpirationSeconds(), UserResponse.from(user));
    }

    private String createAndSaveRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString() + "-" + UUID.randomUUID();
        String tokenHash = hashToken(rawToken);
        Instant expiresAt = Instant.now().plusSeconds(refreshTokenExpirationSeconds);

        RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(), user, tokenHash, expiresAt);
        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 não disponível", e);
        }
    }
}

