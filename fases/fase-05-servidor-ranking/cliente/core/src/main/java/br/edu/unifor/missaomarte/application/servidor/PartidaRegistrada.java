package br.edu.unifor.missaomarte.application.servidor;

import br.edu.unifor.missaomarte.domain.Dificuldade;

/** Uma partida como o servidor guardou; dataHora no formato yyyy-MM-ddTHH:mm:ss do servidor. */
public record PartidaRegistrada(long id, Dificuldade dificuldade, boolean vitoria, int pontuacao,
                                long duracaoSegundos, int movimentos, String dataHora) {
}
