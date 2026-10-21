package service;

import model.Cliente;
import model.Pagamento;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/** Simulação local; uma cobrança por cliente e competência durante esta execução. */
public class PagamentoService implements ProcessadorPagamento {
    private final Map<Cliente, Map<YearMonth, Pagamento>> pagamentos = new IdentityHashMap<>();

    public Pagamento processarPagamento(Cliente cliente) {
        return processarPagamento(cliente, YearMonth.now());
    }

    @Override
    public Pagamento processarPagamento(Cliente cliente, YearMonth competencia) {
        if (cliente == null) throw new IllegalArgumentException("Cliente inválido.");
        if (competencia == null) throw new IllegalArgumentException("Competência é obrigatória.");
        if (cliente.getPlano() == null) throw new RegraNegocioException("Cliente sem plano associado.");
        Map<YearMonth, Pagamento> historico = pagamentos.get(cliente);
        if (historico != null && historico.containsKey(competencia)) {
            throw new RegraNegocioException("Mensalidade já paga nesta competência.");
        }
        Pagamento pagamento = new Pagamento(cliente.getNome(), cliente.getPlano().getNome(),
                competencia, cliente.getPlano().calcularMensalidade());
        pagamentos.computeIfAbsent(cliente, chave -> new HashMap<>()).put(competencia, pagamento);
        return pagamento;
    }
}