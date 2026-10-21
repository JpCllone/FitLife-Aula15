package model;

import java.math.BigDecimal;

public final class PlanoPremium extends Plano {
    public PlanoPremium() { super("Premium", new BigDecimal("150.00")); }

    @Override
    public BigDecimal calcularMensalidade() { return getValorBase(); }
}