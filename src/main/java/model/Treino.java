package model;

public final class Treino {
    private final String nome;
    private final String descricao;
    private final int duracaoMinutos;

    public Treino(String nome, String descricao, int duracaoMinutos) {
        this.nome = Validacao.texto(nome, "Nome do treino");
        this.descricao = Validacao.texto(descricao, "Descrição do treino");
        if (duracaoMinutos <= 0) throw new IllegalArgumentException("Duração deve ser positiva.");
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getDuracaoMinutos() { return duracaoMinutos; }

    @Override
    public String toString() { return nome + " | " + descricao + " | " + duracaoMinutos + " min"; }
}