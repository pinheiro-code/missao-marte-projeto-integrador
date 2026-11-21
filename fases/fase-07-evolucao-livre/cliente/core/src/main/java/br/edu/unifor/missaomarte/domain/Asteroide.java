package br.edu.unifor.missaomarte.domain;

/** Perigo parado. */
public class Asteroide implements ElementoMapa {

    private final Posicao posicao;

    public Asteroide(Posicao posicao) {
        this.posicao = posicao;
    }

    @Override
    public Posicao posicao() {
        return posicao;
    }

    @Override
    public char simbolo() {
        return '#';
    }

    @Override
    public String descricao() {
        return "Asteroide";
    }
}
