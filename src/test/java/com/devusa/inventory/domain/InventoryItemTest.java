package com.devusa.inventory.domain;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryItemTest {

    @Test
    void shouldCalculateAvailableQuantityCorrectly() {
        InventoryItem item = new InventoryItem(UUID.randomUUID(), UUID.randomUUID(), 10);

        assertThat(item.getAvailableQuantity()).isEqualTo(10);

        item.reserve(3);
        assertThat(item.getAvailableQuantity()).isEqualTo(7);
        assertThat(item.getReservedQuantity()).isEqualTo(3);
        assertThat(item.getTotalQuantity()).isEqualTo(10);

        item.commitReservation(3);
        assertThat(item.getAvailableQuantity()).isEqualTo(7);
        assertThat(item.getReservedQuantity()).isEqualTo(0);
        assertThat(item.getTotalQuantity()).isEqualTo(7);
    }

    @Test
    void shouldThrowWhenReservingMoreThanAvailable() {
        InventoryItem item = new InventoryItem(UUID.randomUUID(), UUID.randomUUID(), 2);

        assertThatThrownBy(() -> item.reserve(3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    @Test
    void shouldReleaseReservationCorrectly() {
        InventoryItem item = new InventoryItem(UUID.randomUUID(), UUID.randomUUID(), 5);
        item.reserve(2);
        item.releaseReservation(2);

        assertThat(item.getAvailableQuantity()).isEqualTo(5);
        assertThat(item.getReservedQuantity()).isEqualTo(0);
    }
}

