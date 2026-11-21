package br.edu.unifor.missaomarte.domain;

/** Tudo que aparece no mapa informa onde está e como é desenhado. */
public interface ElementoMapa {

    Posicao posicao();

    char simbolo();

    String descricao();
}
