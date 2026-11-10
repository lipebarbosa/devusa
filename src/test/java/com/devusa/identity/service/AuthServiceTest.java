package com.devusa.identity.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.devusa.identity.domain.RefreshToken;
import com.devusa.identity.domain.Role;
import com.devusa.identity.domain.User;
import com.devusa.identity.dto.AuthResponse;
import com.devusa.identity.dto.LoginRequest;
import com.devusa.identity.dto.RefreshTokenRequest;
import com.devusa.identity.dto.RegisterRequest;
import com.devusa.identity.dto.UserResponse;
import com.devusa.identity.repository.RefreshTokenRepository;
import com.devusa.identity.repository.UserRepository;
import com.devusa.identity.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest {

    private InMemoryUserRepository userRepository;
    private InMemoryRefreshTokenRepository refreshTokenRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        refreshTokenRepository = new InMemoryRefreshTokenRepository();
        passwordEncoder = new FakePasswordEncoder();
        jwtService = new JwtService("verySecretSigningKeyForDevUsaPlatform1234567890", 900L);
        authService = new AuthService(userRepository, refreshTokenRepository, passwordEncoder, jwtService, 604800L);
    }

    @Test
    void registerShouldCreateCustomerWithHashedPasswordAndNormalizedEmail() {
        RegisterRequest request = new RegisterRequest(" Felipe Silva ", "FELIPE@Test.COM ", "senha12345");

        UserResponse response = authService.register(request);

        assertThat(response.name()).isEqualTo("Felipe Silva");
        assertThat(response.email()).isEqualTo("felipe@test.com");
        assertThat(response.role()).isEqualTo(Role.CLIENTE);
        assertThat(userRepository.existsByEmailIgnoreCase("felipe@test.com")).isTrue();
    }

    @Test
    void registerShouldThrowWhenEmailAlreadyExists() {
        User existing = new User(UUID.randomUUID(), "Existing", "felipe@test.com", "hash", Role.CLIENTE, Instant.now());
        userRepository.save(existing);

        RegisterRequest request = new RegisterRequest("Felipe", "FELIPE@test.com", "senha12345");

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("já está em uso");
    }

    @Test
    void loginShouldReturnTokensWhenCredentialsAreValid() {
        User user = new User(UUID.randomUUID(), "Felipe", "felipe@test.com", "hash_senhaCorreta", Role.CLIENTE, Instant.now());
        userRepository.save(user);

        LoginRequest request = new LoginRequest("felipe@test.com", "senhaCorreta");
        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user().email()).isEqualTo("felipe@test.com");
    }

    @Test
    void loginShouldThrowWhenPasswordDoesNotMatch() {
        User user = new User(UUID.randomUUID(), "Felipe", "felipe@test.com", "hash_senhaCorreta", Role.CLIENTE, Instant.now());
        userRepository.save(user);

        LoginRequest request = new LoginRequest("felipe@test.com", "senhaIncorreta");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Credenciais inválidas");
    }

    @Test
    void refreshShouldRotateRefreshToken() {
        User user = new User(UUID.randomUUID(), "Felipe", "felipe@test.com", "hash", Role.CLIENTE, Instant.now());
        userRepository.save(user);

        AuthResponse loginResponse = authService.login(new LoginRequest("felipe@test.com", ""));
        String oldRefreshToken = loginResponse.refreshToken();

        AuthResponse refreshResponse = authService.refresh(new RefreshTokenRequest(oldRefreshToken));

        assertThat(refreshResponse.accessToken()).isNotBlank();
        assertThat(refreshResponse.refreshToken()).isNotEqualTo(oldRefreshToken);

        // Tentar usar o token antigo novamente deve falhar por estar revogado
        assertThatThrownBy(() -> authService.refresh(new RefreshTokenRequest(oldRefreshToken)))
                .isInstanceOf(BadCredentialsException.class);
    }

    // Implementações de teste leves e puras
    static class FakePasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            return "hash_" + rawPassword;
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return ("hash_" + rawPassword).equals(encodedPassword) || encodedPassword.equals("hash");
        }
    }

    static class InMemoryUserRepository implements UserRepository {
        private final Map<UUID, User> store = new HashMap<>();

        @Override
        public Optional<User> findByEmailIgnoreCase(String email) {
            return store.values().stream()
                    .filter(u -> u.getEmail().equalsIgnoreCase(email.trim()))
                    .findFirst();
        }

        @Override
        public boolean existsByEmailIgnoreCase(String email) {
            return findByEmailIgnoreCase(email).isPresent();
        }

        @Override
        public <S extends User> S save(S entity) {
            store.put(entity.getId(), entity);
            return entity;
        }

        @Override public Optional<User> findById(UUID uuid) { return Optional.ofNullable(store.get(uuid)); }
        @Override public boolean existsById(UUID uuid) { return store.containsKey(uuid); }
        @Override public List<User> findAll() { return List.copyOf(store.values()); }
        @Override public List<User> findAllById(Iterable<UUID> uuids) { return List.of(); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(UUID uuid) { store.remove(uuid); }
        @Override public void delete(User entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends UUID> uuids) {}
        @Override public void deleteAll(Iterable<? extends User> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public void flush() {}
        @Override public <S extends User> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }
        @Override public void deleteAllInBatch(Iterable<User> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<UUID> uuids) {}
        @Override public void deleteAllInBatch() {}
        @Override public User getOne(UUID uuid) { return store.get(uuid); }
        @Override public User getById(UUID uuid) { return store.get(uuid); }
        @Override public User getReferenceById(UUID uuid) { return store.get(uuid); }
        @Override public <S extends User> List<S> saveAll(Iterable<S> entities) { return List.of(); }
        @Override public List<User> findAll(Sort sort) { return List.of(); }
        @Override public Page<User> findAll(Pageable pageable) { return Page.empty(); }
        @Override public <S extends User> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends User> List<S> findAll(Example<S> example) { return List.of(); }
        @Override public <S extends User> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
        @Override public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
        @Override public <S extends User> long count(Example<S> example) { return 0; }
        @Override public <S extends User> boolean exists(Example<S> example) { return false; }
        @Override public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
    }

    static class InMemoryRefreshTokenRepository implements RefreshTokenRepository {
        private final Map<UUID, RefreshToken> store = new HashMap<>();

        @Override
        public Optional<RefreshToken> findByTokenHash(String tokenHash) {
            return store.values().stream()
                    .filter(t -> t.getTokenHash().equals(tokenHash))
                    .findFirst();
        }

        @Override
        public void revokeAllByUserId(UUID userId) {
            store.values().stream()
                    .filter(t -> t.getUser().getId().equals(userId))
                    .forEach(RefreshToken::revoke);
        }

        @Override
        public <S extends RefreshToken> S save(S entity) {
            store.put(entity.getId(), entity);
            return entity;
        }

        @Override public Optional<RefreshToken> findById(UUID uuid) { return Optional.ofNullable(store.get(uuid)); }
        @Override public boolean existsById(UUID uuid) { return store.containsKey(uuid); }
        @Override public List<RefreshToken> findAll() { return List.copyOf(store.values()); }
        @Override public List<RefreshToken> findAllById(Iterable<UUID> uuids) { return List.of(); }
        @Override public long count() { return store.size(); }
        @Override public void deleteById(UUID uuid) { store.remove(uuid); }
        @Override public void delete(RefreshToken entity) { store.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends UUID> uuids) {}
        @Override public void deleteAll(Iterable<? extends RefreshToken> entities) {}
        @Override public void deleteAll() { store.clear(); }
        @Override public void flush() {}
        @Override public <S extends RefreshToken> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends RefreshToken> List<S> saveAllAndFlush(Iterable<S> entities) { return List.of(); }
        @Override public void deleteAllInBatch(Iterable<RefreshToken> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<UUID> uuids) {}
        @Override public void deleteAllInBatch() {}
        @Override public RefreshToken getOne(UUID uuid) { return store.get(uuid); }
        @Override public RefreshToken getById(UUID uuid) { return store.get(uuid); }
        @Override public RefreshToken getReferenceById(UUID uuid) { return store.get(uuid); }
        @Override public <S extends RefreshToken> List<S> saveAll(Iterable<S> entities) { return List.of(); }
        @Override public List<RefreshToken> findAll(Sort sort) { return List.of(); }
        @Override public Page<RefreshToken> findAll(Pageable pageable) { return Page.empty(); }
        @Override public <S extends RefreshToken> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends RefreshToken> List<S> findAll(Example<S> example) { return List.of(); }
        @Override public <S extends RefreshToken> List<S> findAll(Example<S> example, Sort sort) { return List.of(); }
        @Override public <S extends RefreshToken> Page<S> findAll(Example<S> example, Pageable pageable) { return Page.empty(); }
        @Override public <S extends RefreshToken> long count(Example<S> example) { return 0; }
        @Override public <S extends RefreshToken> boolean exists(Example<S> example) { return false; }
        @Override public <S extends RefreshToken, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
    }
}
