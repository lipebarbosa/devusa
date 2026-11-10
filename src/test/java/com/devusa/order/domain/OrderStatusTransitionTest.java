package com.devusa.order.domain;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTransitionTest {

    @Test
    void allowsOnlyTransitionsDefinedByOrderFlow() {
        Set<Transition> expectedTransitions = Set.of(
                new Transition(OrderStatus.AGUARDANDO_PAGAMENTO, OrderStatus.PAGO),
                new Transition(OrderStatus.AGUARDANDO_PAGAMENTO, OrderStatus.CANCELADO),
                new Transition(OrderStatus.AGUARDANDO_PAGAMENTO, OrderStatus.EXPIRADO),
                new Transition(OrderStatus.PAGO, OrderStatus.EM_SEPARACAO),
                new Transition(OrderStatus.EM_SEPARACAO, OrderStatus.ENVIADO),
                new Transition(OrderStatus.ENVIADO, OrderStatus.ENTREGUE)
        );

        Set<Transition> actualTransitions = Arrays.stream(OrderStatus.values())
                .flatMap(from -> Arrays.stream(OrderStatus.values())
                        .filter(from::canTransitionTo)
                        .map(to -> new Transition(from, to)))
                .collect(Collectors.toSet());

        assertThat(actualTransitions).containsExactlyInAnyOrderElementsOf(expectedTransitions);
    }

    private record Transition(OrderStatus from, OrderStatus to) {
    }
}
