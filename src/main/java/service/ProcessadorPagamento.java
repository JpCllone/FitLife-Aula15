package service;

import model.Cliente;
import model.Pagamento;
import java.time.YearMonth;

public interface ProcessadorPagamento {
    Pagamento processarPagamento(Cliente cliente, YearMonth competencia);
}