package model;

import java.math.BigDecimal;

public final class PlanoMensal extends Plano {
    public PlanoMensal() { super("Mensal", new BigDecimal("100.00")); }

    @Override
    public BigDecimal calcularMensalidade() { return getValorBase(); }
}