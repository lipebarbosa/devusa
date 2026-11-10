package com.devusa.order.domain;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void identifiesFinalStatuses() {
        assertThat(OrderStatus.values()).containsExactly(
                OrderStatus.AGUARDANDO_PAGAMENTO,
                OrderStatus.PAGO,
                OrderStatus.EM_SEPARACAO,
                OrderStatus.ENVIADO,
                OrderStatus.ENTREGUE,
                OrderStatus.CANCELADO,
                OrderStatus.EXPIRADO
        );

        assertThat(Arrays.stream(OrderStatus.values())
                        .filter(OrderStatus::isFinal)
                        .toList())
                .containsExactlyInAnyOrder(
                        OrderStatus.ENTREGUE,
                        OrderStatus.CANCELADO,
                        OrderStatus.EXPIRADO
                );
    }
}
