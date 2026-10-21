package service;

import model.*;
import java.util.ArrayList;
import java.util.List;

/** Evolução do serviço do projeto FitLife_Academia anterior. IDs começam em 1. */
public class AcademiaService {
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Plano> planos = new ArrayList<>();

    public Cliente cadastrarCliente(String nome, String email, int idade) {
        Cliente cliente = new Cliente(nome, email, idade);
        clientes.add(cliente);
        return cliente;
    }

    public Plano cadastrarPlano(Plano plano) {
        if (plano == null) throw new IllegalArgumentException("Plano é obrigatório.");
        if (planos.stream().anyMatch(p -> p.getNome().equalsIgnoreCase(plano.getNome()))) {
            throw new RegraNegocioException("Plano já cadastrado.");
        }
        planos.add(plano);
        return plano;
    }

    public Cliente consultarCliente(int id) {
        if (id < 1 || id > clientes.size()) throw new RegraNegocioException("Cliente não encontrado.");
        return clientes.get(id - 1);
    }

    public Plano consultarPlano(int id) {
        if (id < 1 || id > planos.size()) throw new RegraNegocioException("Plano não encontrado.");
        return planos.get(id - 1);
    }

    public void associarPlano(int clienteId, int planoId) {
        Cliente cliente = consultarCliente(clienteId);
        Plano plano = consultarPlano(planoId);
        cliente.setPlano(plano);
    }

    public Treino cadastrarTreino(int clienteId, String nome, String descricao, int minutos) {
        Cliente cliente = consultarCliente(clienteId);
        Treino treino = new Treino(nome, descricao, minutos);
        cliente.adicionarTreino(treino);
        return treino;
    }

    public List<Cliente> listarClientes() { return List.copyOf(clientes); }
    public List<Plano> listarPlanos() { return List.copyOf(planos); }
}