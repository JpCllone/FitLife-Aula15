package fitlife;

import model.*;
import service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import static org.junit.jupiter.api.Assertions.*;

class FitLifeTest {
    private Cliente cliente() { return new Cliente("Ana", "ana@email.com", 15); }
    private final YearMonth outubro = YearMonth.of(2026, 10);

    @Test void aceitaIdadeMinimaENormalizaEspacos() {
        Cliente c = new Cliente(" Ana ", " ana@email.com ", 15);
        assertAll(() -> assertEquals("Ana", c.getNome()), () -> assertEquals("ana@email.com", c.getEmail()),
                () -> assertEquals(15, c.getIdade()), () -> assertNull(c.getPlano()));
    }

    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" ", "\t\n"})
    void rejeitaNomeVazio(String nome) {
        assertThrows(IllegalArgumentException.class, () -> new Cliente(nome, "a@email.com", 15));
    }

    @ParameterizedTest @NullAndEmptySource
    @ValueSource(strings = {" ", "sem-arroba", "a@", "@email.com", "a b@email.com", "a@email", "a..b@email.com", "a@-email.com"})
    void rejeitaEmailInvalido(String email) {
        assertThrows(IllegalArgumentException.class, () -> new Cliente("Ana", email, 15));
    }

    @ParameterizedTest @ValueSource(ints = {-1, 0, 14})
    void rejeitaIdadeMenorQue15(int idade) {
        assertThrows(IllegalArgumentException.class, () -> new Cliente("Ana", "ana@email.com", idade));
    }

    @Test void settersInvalidosPreservamEstado() {
        Cliente c = cliente();
        assertThrows(IllegalArgumentException.class, () -> c.setNome(" "));
        assertThrows(IllegalArgumentException.class, () -> c.setEmail("invalido"));
        assertThrows(IllegalArgumentException.class, () -> c.setIdade(14));
        assertAll(() -> assertEquals("Ana", c.getNome()), () -> assertEquals("ana@email.com", c.getEmail()),
                () -> assertEquals(15, c.getIdade()));
        c.setNome("Bia"); c.setEmail("bia+teste@email.com"); c.setIdade(18);
        assertEquals("Bia", c.getNome()); assertEquals("bia+teste@email.com", c.getEmail()); assertEquals(18, c.getIdade());
    }

    @Test void calculaMensalidadesPorPolimorfismo() {
        Plano[] planos = {new PlanoMensal(), new PlanoPremium()};
        assertEquals(new BigDecimal("100.00"), planos[0].calcularMensalidade());
        assertEquals(new BigDecimal("150.00"), planos[1].calcularMensalidade());
    }

    @Test void validaBaseDoPlano() {
        assertThrows(IllegalArgumentException.class, () -> plano(" ", BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> plano("Teste", null));
        assertThrows(IllegalArgumentException.class, () -> plano("Teste", BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> plano("Teste", new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class, () -> plano("Teste", new BigDecimal("1.001")));
    }

    private Plano plano(String nome, BigDecimal valor) {
        return new Plano(nome, valor) {
            @Override public BigDecimal calcularMensalidade() { return getValorBase(); }
        };
    }

    @Test void cadastraAssociaETrocaPlano() {
        AcademiaService academia = new AcademiaService();
        Cliente c = academia.cadastrarCliente("Ana", "ana@email.com", 15);
        Plano mensal = academia.cadastrarPlano(new PlanoMensal());
        Plano premium = academia.cadastrarPlano(new PlanoPremium());
        academia.associarPlano(1, 1); assertSame(mensal, c.getPlano());
        academia.associarPlano(1, 2); assertSame(premium, c.getPlano());
        assertThrows(IllegalArgumentException.class, () -> c.setPlano(null));
        assertSame(premium, c.getPlano());
    }

    @Test void falhasDeCadastroNaoAdicionamRegistros() {
        AcademiaService a = new AcademiaService();
        assertThrows(IllegalArgumentException.class, () -> a.cadastrarCliente("Ana", "x", 15));
        assertTrue(a.listarClientes().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> a.cadastrarPlano(null));
        a.cadastrarPlano(new PlanoMensal());
        assertThrows(RegraNegocioException.class, () -> a.cadastrarPlano(new PlanoMensal()));
        assertEquals(1, a.listarPlanos().size());
    }

    @Test void rejeitaIdsDesconhecidosSemAlterarAssociacao() {
        AcademiaService a = new AcademiaService();
        Cliente c = a.cadastrarCliente("Ana", "ana@email.com", 15);
        a.cadastrarPlano(new PlanoMensal()); a.associarPlano(1, 1);
        assertThrows(RegraNegocioException.class, () -> a.consultarCliente(0));
        assertThrows(RegraNegocioException.class, () -> a.associarPlano(2, 1));
        assertThrows(RegraNegocioException.class, () -> a.associarPlano(1, 2));
        assertThrows(RegraNegocioException.class, () -> a.consultarPlano(-1));
        assertEquals("Mensal", c.getPlano().getNome());
    }

    @Test void cadastraTreinoSomenteNoClienteCorretoEProtegeListas() {
        AcademiaService a = new AcademiaService();
        Cliente c = a.cadastrarCliente("Ana", "ana@email.com", 15);
        Cliente outro = a.cadastrarCliente("Bia", "bia@email.com", 20);
        a.cadastrarPlano(new PlanoMensal());
        Treino t = a.cadastrarTreino(1, " A ", " Força ", 45);
        assertEquals("A", t.getNome()); assertEquals("Força", t.getDescricao());
        assertEquals(45, t.getDuracaoMinutos()); assertSame(t, c.getTreinos().get(0));
        assertTrue(outro.getTreinos().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> c.getTreinos().clear());
        assertThrows(UnsupportedOperationException.class, () -> a.listarClientes().clear());
        assertThrows(UnsupportedOperationException.class, () -> a.listarPlanos().clear());
        assertThrows(IllegalArgumentException.class, () -> c.adicionarTreino(null));
        assertThrows(RegraNegocioException.class, () -> a.cadastrarTreino(9, "A", "Força", 30));
        assertThrows(IllegalArgumentException.class, () -> a.cadastrarTreino(1, "A", "Força", 0));
        assertEquals(1, c.getTreinos().size());
    }

    @Test void validaDadosDoTreino() {
        assertThrows(IllegalArgumentException.class, () -> new Treino(null, "Força", 30));
        assertThrows(IllegalArgumentException.class, () -> new Treino(" ", "Força", 30));
        assertThrows(IllegalArgumentException.class, () -> new Treino("A", null, 30));
        assertThrows(IllegalArgumentException.class, () -> new Treino("A", " ", 30));
        assertThrows(IllegalArgumentException.class, () -> new Treino("A", "Força", -1));
    }

    @Test void pagamentoValidaClientePlanoECompetencia() {
        PagamentoService p = new PagamentoService(); Cliente c = cliente();
        assertThrows(IllegalArgumentException.class, () -> p.processarPagamento(null, outubro));
        assertThrows(IllegalArgumentException.class, () -> p.processarPagamento(c, null));
        assertThrows(RegraNegocioException.class, () -> p.processarPagamento(c, outubro));
        c.setPlano(new PlanoMensal());
        assertEquals(new BigDecimal("100.00"), p.processarPagamento(c, outubro).valor());
    }

    @Test void bloqueiaDuplicidadePermiteProximoMesEPreservaRecibo() {
        ProcessadorPagamento p = new PagamentoService(); Cliente c = cliente();
        c.setPlano(new PlanoMensal());
        Pagamento recibo = p.processarPagamento(c, outubro);
        c.setPlano(new PlanoPremium()); c.setNome("Ana Maria");
        assertThrows(RegraNegocioException.class, () -> p.processarPagamento(c, outubro));
        assertEquals(new BigDecimal("150.00"), p.processarPagamento(c, outubro.plusMonths(1)).valor());
        assertEquals("Ana", recibo.cliente()); assertEquals("Mensal", recibo.plano());
        assertEquals(outubro, recibo.competencia()); assertEquals(new BigDecimal("100.00"), recibo.valor());
        Cliente outro = cliente(); outro.setPlano(new PlanoMensal());
        assertDoesNotThrow(() -> p.processarPagamento(outro, outubro));
    }

    @Test void reciboInvalidoNaoPodeSerCriado() {
        assertThrows(IllegalArgumentException.class, () -> new Pagamento("Ana", "Mensal", null, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class, () -> new Pagamento("Ana", "Mensal", outubro, BigDecimal.ZERO));
    }

    @Test void fluxoCompletoDaAtividade() {
        AcademiaService a = new AcademiaService();
        Cliente c = a.cadastrarCliente("Ana", "ana@email.com", 15);
        a.cadastrarPlano(new PlanoPremium()); a.associarPlano(1, 1);
        a.cadastrarTreino(1, "A", "Força", 40);
        Pagamento pagamento = new PagamentoService().processarPagamento(c, outubro);
        assertEquals(1, c.getTreinos().size());
        assertEquals(new BigDecimal("150.00"), pagamento.valor());
        assertTrue(pagamento.toString().contains("Pagamento realizado com sucesso!"));
    }
}