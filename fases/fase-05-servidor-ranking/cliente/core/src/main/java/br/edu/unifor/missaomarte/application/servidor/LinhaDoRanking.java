package br.edu.unifor.missaomarte.application.servidor;

import br.edu.unifor.missaomarte.domain.Dificuldade;

/** Uma linha do ranking global: a melhor vitória de um jogador. */
public record LinhaDoRanking(int posicao, String nome, int pontuacao, Dificuldade dificuldade,
                             long duracaoSegundos, int movimentos, String dataHora) {
}
