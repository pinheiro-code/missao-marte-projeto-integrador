package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;

/** Como as partidas do servidor aparecem na tela, sem libGDX. */
public final class FormatoDoHistorico {

    private FormatoDoHistorico() {
    }

    /** 2026-11-21T14:03:05 vira 21/11/2026 14:03; formato desconhecido volta como veio. */
    public static String dataHora(String doServidor) {
        if (doServidor == null || doServidor.length() < 16 || doServidor.charAt(10) != 'T') {
            return doServidor == null ? "" : doServidor;
        }
        return doServidor.substring(8, 10) + "/" + doServidor.substring(5, 7) + "/" + doServidor.substring(0, 4)
                + " " + doServidor.substring(11, 16);
    }

    public static String linha(PartidaRegistrada partida) {
        return dataHora(partida.dataHora()) + "   " + (partida.vitoria() ? "VITÓRIA" : "DERROTA") + "   "
                + partida.dificuldade() + "   " + partida.pontuacao() + " pts   " + partida.duracaoSegundos()
                + " s   " + partida.movimentos() + " mov.";
    }

    public static String linha(LinhaDoRanking linha) {
        return linha.posicao() + ". " + linha.nome() + " - " + linha.pontuacao() + " pts | " + linha.dificuldade()
                + " | " + linha.duracaoSegundos() + " s | " + linha.movimentos() + " mov. | "
                + dataHora(linha.dataHora());
    }
}
