package br.edu.unifor.missaomarte.domain;

public class Professor extends Passageiro {

    public Professor(String nome, Posicao posicao) {
        super(nome, posicao);
    }

    @Override
    public int pontuacao() {
        return 10;
    }

    @Override
    public char simbolo() {
        return 'P';
    }

    @Override
    public String descricao() {
        return "Professor";
    }
}
