package br.edu.unifor.missaomarte.application.servidor;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Dificuldade;

/** O que vai para o servidor no fim da partida. */
public record PartidaConcluida(Dificuldade dificuldade, boolean vitoria, int pontuacao, long duracaoSegundos,
                               int movimentos) {

    public static PartidaConcluida de(GameSession sessao) {
        if (!sessao.acabou()) {
            throw new IllegalStateException("A partida ainda não acabou.");
        }
        return new PartidaConcluida(sessao.dificuldade(), sessao.venceu(), sessao.pontos(),
                sessao.duracaoSegundos(), sessao.movimentos());
    }
}
