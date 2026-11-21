package br.edu.unifor.missaomarte.domain;

public class Engenheiro extends Passageiro {

    public Engenheiro(String nome, Posicao posicao) {
        super(nome, posicao);
    }

    @Override
    public int pontuacao() {
        return 15;
    }

    @Override
    public char simbolo() {
        return 'E';
    }

    @Override
    public String descricao() {
        return "Engenheiro";
    }
}
