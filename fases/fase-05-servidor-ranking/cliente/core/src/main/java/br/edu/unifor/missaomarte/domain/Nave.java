package br.edu.unifor.missaomarte.domain;

import java.util.ArrayList;
import java.util.List;

public class Nave implements ElementoMapa {

    private static final int VIDAS_INICIAIS = 3;

    private final int capacidade;
    private final List<Passageiro> aBordo = new ArrayList<>();
    private Posicao posicao = Posicao.ORIGEM;
    private int vidas = VIDAS_INICIAIS;

    public Nave(int capacidade) {
        this.capacidade = capacidade;
    }

    /** Movimento que sairia do mapa deixa a nave no lugar. */
    public void mover(Acao acao, int tamanho) {
        Posicao nova = posicao.mover(acao.dx(), acao.dy());
        if (nova.dentroDoMapa(tamanho)) {
            posicao = nova;
        }
    }

    public boolean embarcar(Passageiro passageiro) {
        if (cheia()) {
            return false;
        }
        aBordo.add(passageiro);
        return true;
    }

    /** Esvazia a nave e devolve quantos desceram. */
    public int desembarcar() {
        int quantidade = aBordo.size();
        aBordo.clear();
        return quantidade;
    }

    public boolean cheia() {
        return aBordo.size() >= capacidade;
    }

    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }
    }

    public int vidas() {
        return vidas;
    }

    public int capacidade() {
        return capacidade;
    }

    public int quantidadeABordo() {
        return aBordo.size();
    }

    @Override
    public Posicao posicao() {
        return posicao;
    }

    @Override
    public char simbolo() {
        return '@';
    }

    @Override
    public String descricao() {
        return "Nave";
    }
}
