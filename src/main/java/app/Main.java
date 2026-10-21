package app;

import model.*;
import service.*;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Main {
    public static void main(String[] args) {
        System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));
        if (args.length > 0 && args[0].equals("--demo")) {
            demonstrar();
            return;
        }
        AcademiaService academia = new AcademiaService();
        ProcessadorPagamento pagamentos = new PagamentoService();
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (true) {
                System.out.println("\nFITLIFE — Aula 15 / Unidade 4");
                System.out.println("1 Cadastrar cliente | 2 Cadastrar plano | 3 Associar plano");
                System.out.println("4 Cadastrar treino | 5 Pagar mensalidade | 6 Listar clientes");
                System.out.println("7 Listar planos | 8 Ver treinos | 0 Sair");
                try {
                    int opcao = inteiro(scanner, "Opção: ");
                    switch (opcao) {
                        case 0 -> { System.out.println("Sistema encerrado."); return; }
                        case 1 -> {
                            String nome = ler(scanner, "Nome: ");
                            String email = ler(scanner, "E-mail: ");
                            int idade = inteiro(scanner, "Idade: ");
                            academia.cadastrarCliente(nome, email, idade);
                            System.out.println("Cliente cadastrado! ID: " + academia.listarClientes().size());
                        }
                        case 2 -> {
                            int tipo = inteiro(scanner, "1 Mensal (R$ 100,00) | 2 Premium (R$ 150,00): ");
                            Plano plano = switch (tipo) {
                                case 1 -> new PlanoMensal();
                                case 2 -> new PlanoPremium();
                                default -> throw new IllegalArgumentException("Tipo de plano inválido.");
                            };
                            academia.cadastrarPlano(plano);
                            System.out.println("Plano cadastrado! ID: " + academia.listarPlanos().size());
                        }
                        case 3 -> {
                            academia.associarPlano(inteiro(scanner, "ID do cliente: "), inteiro(scanner, "ID do plano: "));
                            System.out.println("Plano associado com sucesso!");
                        }
                        case 4 -> {
                            int id = inteiro(scanner, "ID do cliente: ");
                            String nome = ler(scanner, "Nome do treino: ");
                            String descricao = ler(scanner, "Descrição: ");
                            System.out.println("Treino cadastrado: " + academia.cadastrarTreino(id, nome, descricao,
                                    inteiro(scanner, "Duração em minutos: ")));
                        }
                        case 5 -> {
                            Cliente cliente = academia.consultarCliente(inteiro(scanner, "ID do cliente: "));
                            YearMonth competencia = YearMonth.parse(ler(scanner, "Competência (AAAA-MM): "));
                            System.out.println(pagamentos.processarPagamento(cliente, competencia));
                        }
                        case 6 -> listar(academia.listarClientes());
                        case 7 -> listar(academia.listarPlanos());
                        case 8 -> listar(academia.consultarCliente(inteiro(scanner, "ID do cliente: ")).getTreinos());
                        default -> System.out.println("Opção inválida.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Erro: digite um número inteiro válido.");
                } catch (DateTimeParseException e) {
                    System.out.println("Erro: use uma competência válida no formato AAAA-MM.");
                } catch (IllegalArgumentException | RegraNegocioException e) {
                    System.out.println("Erro: " + e.getMessage());
                } catch (NoSuchElementException e) {
                    System.out.println("\nEntrada encerrada. Até logo!");
                    return;
                }
            }
        }
    }

    private static String ler(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().strip();
    }

    private static int inteiro(Scanner scanner, String mensagem) {
        return Integer.parseInt(ler(scanner, mensagem));
    }

    private static void listar(List<?> itens) {
        if (itens.isEmpty()) System.out.println("Nenhum registro cadastrado.");
        for (int i = 0; i < itens.size(); i++) System.out.println((i + 1) + " - " + itens.get(i));
    }

    public static void demonstrar() {
        AcademiaService academia = new AcademiaService();
        ProcessadorPagamento pagamentos = new PagamentoService();
        academia.cadastrarPlano(new PlanoMensal());
        academia.cadastrarPlano(new PlanoPremium());
        Cliente joao = academia.cadastrarCliente("João Silva", "joao@email.com", 20);
        Cliente maria = academia.cadastrarCliente("Maria Souza", "maria@email.com", 15);
        academia.associarPlano(1, 1);
        academia.associarPlano(2, 2);
        academia.cadastrarTreino(1, "Treino A", "Musculação de membros superiores", 45);
        academia.cadastrarTreino(2, "Treino B", "Condicionamento físico", 30);
        System.out.println("FITLIFE — Demonstração Aula 15 / Unidade 4");
        listar(academia.listarPlanos());
        listar(academia.listarClientes());
        System.out.println("Treinos de João:");
        listar(joao.getTreinos());
        YearMonth competencia = YearMonth.of(2026, 10);
        System.out.println(pagamentos.processarPagamento(joao, competencia));
        System.out.println(pagamentos.processarPagamento(maria, competencia));
        try {
            academia.cadastrarCliente("Pedro", "pedro@email.com", 14);
        } catch (IllegalArgumentException e) {
            System.out.println("Validação esperada: " + e.getMessage());
        }
        try {
            pagamentos.processarPagamento(joao, competencia);
        } catch (RegraNegocioException e) {
            System.out.println("Validação esperada: " + e.getMessage());
        }
    }
}