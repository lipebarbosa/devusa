package com.devusa.order.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTransitionTest {

    @Test
    void awaitingPaymentCanTransitionToPaid() {
        assertThat(OrderStatus.AGUARDANDO_PAGAMENTO.canTransitionTo(OrderStatus.PAGO)).isTrue();
    }
}
