package br.edu.unifor.missaomarte.domain;

public class Astronauta extends Passageiro {

    public Astronauta(String nome, Posicao posicao) {
        super(nome, posicao);
    }

    @Override
    public int pontuacao() {
        return 20;
    }

    @Override
    public char simbolo() {
        return 'T';
    }

    @Override
    public String descricao() {
        return "Astronauta";
    }
}
