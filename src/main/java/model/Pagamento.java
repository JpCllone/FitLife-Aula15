package model;

import java.math.BigDecimal;
import java.time.YearMonth;

/** Recibo imutável: preserva os dados mesmo após a troca do plano do cliente. */
public record Pagamento(String cliente, String plano, YearMonth competencia, BigDecimal valor) {
    public Pagamento {
        cliente = Validacao.texto(cliente, "Cliente");
        plano = Validacao.texto(plano, "Plano");
        if (competencia == null || valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("Competência e valor positivo são obrigatórios.");
        }
    }

    @Override
    public String toString() {
        return "Pagamento realizado com sucesso! Cliente: " + cliente + " | Plano: " + plano
                + " | Competência: " + competencia + " | Valor: R$ " + valor.toPlainString().replace('.', ',');
    }
}