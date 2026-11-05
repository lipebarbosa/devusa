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
        return switch (this) {
            case AGUARDANDO_PAGAMENTO ->
                    nextStatus == PAGO || nextStatus == CANCELADO || nextStatus == EXPIRADO;
            case PAGO -> nextStatus == EM_SEPARACAO;
            case EM_SEPARACAO -> nextStatus == ENVIADO;
            case ENVIADO -> nextStatus == ENTREGUE;
            case ENTREGUE, CANCELADO, EXPIRADO -> false;
        };
    }
}
