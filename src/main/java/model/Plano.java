package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Plano {
    private final String nome;
    private final BigDecimal valorBase;

    protected Plano(String nome, BigDecimal valorBase) {
        this.nome = Validacao.texto(nome, "Nome do plano");
        if (valorBase == null || valorBase.signum() <= 0) {
            throw new IllegalArgumentException("Valor do plano deve ser positivo.");
        }
        try {
            this.valorBase = valorBase.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Valor deve ter no máximo duas casas decimais.", e);
        }
    }

    public String getNome() { return nome; }
    public BigDecimal getValorBase() { return valorBase; }
    public abstract BigDecimal calcularMensalidade();

    @Override
    public String toString() {
        return nome + " | R$ " + calcularMensalidade().toPlainString().replace('.', ',');
    }
}