package br.edu.unifor.missaomarte.application.servidor;

/** Quem entrou: nome e token. Vive só na memória e o toString não mostra o token. */
public final class Sessao {

    private final String nome;
    private final String token;

    public Sessao(String nome, String token) {
        this.nome = nome;
        this.token = token;
    }

    public String nome() {
        return nome;
    }

    public String token() {
        return token;
    }

    @Override
    public String toString() {
        return "Sessao[nome=" + nome + "]";
    }
}
