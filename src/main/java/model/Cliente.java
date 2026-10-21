package model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Cliente {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9_%+\\-]+(?:\\.[A-Za-z0-9_%+\\-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z]{2,}$");
    private String nome;
    private String email;
    private int idade;
    private Plano plano;
    private final List<Treino> treinos = new ArrayList<>();

    public Cliente(String nome, String email, int idade) {
        setNome(nome);
        setEmail(email);
        setIdade(idade);
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public int getIdade() { return idade; }
    public Plano getPlano() { return plano; }
    public List<Treino> getTreinos() { return List.copyOf(treinos); }

    public void setNome(String nome) { this.nome = Validacao.texto(nome, "Nome"); }

    public void setEmail(String email) {
        String valor = Validacao.texto(email, "E-mail");
        if (!EMAIL.matcher(valor).matches()) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        this.email = valor;
    }

    public void setIdade(int idade) {
        if (idade < 15) throw new IllegalArgumentException("Idade mínima é 15 anos.");
        this.idade = idade;
    }

    public void setPlano(Plano plano) {
        if (plano == null) throw new IllegalArgumentException("Plano é obrigatório.");
        this.plano = plano;
    }

    public void adicionarTreino(Treino treino) {
        if (treino == null) throw new IllegalArgumentException("Treino é obrigatório.");
        treinos.add(treino);
    }

    @Override
    public String toString() {
        return nome + " | " + email + " | " + idade + " anos | Plano: "
                + (plano == null ? "não associado" : plano.getNome()) + " | Treinos: " + treinos.size();
    }
}