package br.edu.unifor.missaomarte.domain;

public abstract class Passageiro implements ElementoMapa {

    private final String nome;
    private final Posicao posicao;

    protected Passageiro(String nome, Posicao posicao) {
        this.nome = nome;
        this.posicao = posicao;
    }

    public String nome() {
        return nome;
    }

    @Override
    public Posicao posicao() {
        return posicao;
    }

    /** Bônus somado aos pontos no embarque. */
    public abstract int pontuacao();
}
