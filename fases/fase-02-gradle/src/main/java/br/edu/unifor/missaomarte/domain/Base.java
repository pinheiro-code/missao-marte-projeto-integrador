package br.edu.unifor.missaomarte.domain;

/** Plataforma de pouso: onde a nave nasce e entrega os passageiros. */
public class Base implements ElementoMapa {

    @Override
    public Posicao posicao() {
        return Posicao.ORIGEM;
    }

    @Override
    public char simbolo() {
        return 'L';
    }

    @Override
    public String descricao() {
        return "Plataforma de Pouso";
    }
}
