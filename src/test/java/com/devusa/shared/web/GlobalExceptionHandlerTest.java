package com.devusa.shared.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldReturnConflictProblemDetailForIllegalState() {
        ProblemDetail problem = handler.handleIllegalState(new IllegalStateException("Transição inválida"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("Business Rule Conflict");
        assertThat(problem.getDetail()).isEqualTo("Transição inválida");
        assertThat(problem.getProperties()).containsEntry("code", "BUSINESS_CONFLICT");
    }

    @Test
    void shouldReturnUnauthorizedProblemDetailForBadCredentials() {
        ProblemDetail problem = handler.handleBadCredentials(new BadCredentialsException("Credenciais inválidas."));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(problem.getProperties()).containsEntry("code", "UNAUTHORIZED");
    }

    @Test
    void shouldReturnForbiddenProblemDetailForAccessDenied() {
        ProblemDetail problem = handler.handleAccessDenied(new AccessDeniedException("Denied"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(problem.getProperties()).containsEntry("code", "FORBIDDEN");
    }
}

