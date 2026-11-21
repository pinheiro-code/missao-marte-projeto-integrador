package br.edu.unifor.missaomarte.domain;

import java.util.Random;

/** Perigo que anda uma casa por turno, em direção sorteada. */
public class Inimigo implements ElementoMapa {

    private static final int[][] DIRECOES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private Posicao posicao;

    public Inimigo(Posicao posicao) {
        this.posicao = posicao;
    }

    /** Se a direção sorteada sai do mapa, fica parado neste turno. */
    public void mover(Random random, int tamanho) {
        int[] direcao = DIRECOES[random.nextInt(DIRECOES.length)];
        Posicao nova = posicao.mover(direcao[0], direcao[1]);
        if (nova.dentroDoMapa(tamanho)) {
            posicao = nova;
        }
    }

    @Override
    public Posicao posicao() {
        return posicao;
    }

    @Override
    public char simbolo() {
        return 'X';
    }

    @Override
    public String descricao() {
        return "Inimigo";
    }
}
