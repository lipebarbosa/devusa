package com.devusa.order.domain;

public enum OrderStatus {
    AGUARDANDO_PAGAMENTO,
    PAGO,
    EM_SEPARACAO,
    ENVIADO,
    ENTREGUE,
    CANCELADO,
    EXPIRADO;

    public boolean isFinal() {
        return switch (this) {
            case ENTREGUE, CANCELADO, EXPIRADO -> true;
            case AGUARDANDO_PAGAMENTO, PAGO, EM_SEPARACAO, ENVIADO -> false;
        };
    }

    public boolean canTransitionTo(OrderStatus nextStatus) {
        return this == AGUARDANDO_PAGAMENTO && nextStatus == PAGO;
    }
}
