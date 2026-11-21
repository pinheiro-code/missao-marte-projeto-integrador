package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Astronauta;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Engenheiro;
import br.edu.unifor.missaomarte.domain.Inimigo;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Passageiro;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import java.util.Random;

/** Monta o cenário de uma dificuldade, sorteando posições livres. */
public class MissaoFactory {

    private static final int CAPACIDADE_NAVE = 5;

    private final Random random;

    public MissaoFactory(Random random) {
        this.random = random;
    }

    public Missao criar(Dificuldade dificuldade, int tamanho) {
        if (tamanho < dificuldade.tamanhoMinimo()) {
            throw new IllegalArgumentException(
                    "Mapa " + tamanho + " é menor que o mínimo " + dificuldade.tamanhoMinimo() + " de " + dificuldade);
        }
        Missao missao = new Missao(tamanho, new Nave(CAPACIDADE_NAVE));
        for (int i = 0; i < dificuldade.passageiros(); i++) {
            missao.adicionar(criarPassageiro(i, posicaoLivre(missao)));
        }
        for (int i = 0; i < dificuldade.asteroides(); i++) {
            missao.adicionar(new Asteroide(posicaoLivre(missao)));
        }
        for (int i = 0; i < dificuldade.inimigos(); i++) {
            missao.adicionar(new Inimigo(posicaoLivre(missao)));
        }
        return missao;
    }

    private Posicao posicaoLivre(Missao missao) {
        int lado = 2 * missao.tamanho() + 1;
        Posicao posicao;
        do {
            posicao = new Posicao(random.nextInt(lado) - missao.tamanho(), random.nextInt(lado) - missao.tamanho());
        } while (missao.ocupada(posicao));
        return posicao;
    }

    /** Mesma sequência de nomes do jogo original. */
    private Passageiro criarPassageiro(int indice, Posicao posicao) {
        return switch (indice % 5) {
            case 0 -> new Professor("Dr. Silva", posicao);
            case 1 -> new Engenheiro("Eng. Rosa", posicao);
            case 2 -> new Professor("Dr. Lima", posicao);
            case 3 -> new Engenheiro("Eng. Carlos", posicao);
            default -> new Astronauta("Ast. Maria", posicao);
        };
    }
}
