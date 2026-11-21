package br.edu.unifor.missaomarte.application;

import br.edu.unifor.missaomarte.domain.Dificuldade;

/** Uma linha do ranking. Os nomes dos campos são as chaves do ranking.json. */
public record RankingEntry(String name, int score, Dificuldade dificuldade, int passageirosColetados,
                           String dataHora, long tempoJogo) {
}
