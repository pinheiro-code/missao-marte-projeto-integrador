package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import org.junit.jupiter.api.Test;

class FormatoDoHistoricoTest {

    @Test
    void dataDoServidorViraDiaMesAnoEHora() {
        assertEquals("21/11/2026 14:03", FormatoDoHistorico.dataHora("2026-11-21T14:03:05"));
        assertEquals("01/02/2027 09:00", FormatoDoHistorico.dataHora("2027-02-01T09:00:59"));
    }

    @Test
    void formatoDesconhecidoVoltaComoVeio() {
        assertEquals("ontem", FormatoDoHistorico.dataHora("ontem"));
        assertEquals("", FormatoDoHistorico.dataHora(null));
    }

    @Test
    void linhaDoHistoricoTemTudo() {
        PartidaRegistrada partida = new PartidaRegistrada(1, Dificuldade.MEDIO, false, -3, 25, 23,
                "2026-11-21T14:03:05");

        assertEquals("21/11/2026 14:03   DERROTA   Médio   -3 pts   25 s   23 mov.", FormatoDoHistorico.linha(partida));
    }

    @Test
    void linhaDoRankingTemPosicaoENome() {
        LinhaDoRanking linha = new LinhaDoRanking(2, "piloto_1", 120, Dificuldade.DIFICIL, 40, 18,
                "2026-11-21T14:03:05");

        assertEquals("2. piloto_1 - 120 pts | Difícil | 40 s | 18 mov. | 21/11/2026 14:03",
                FormatoDoHistorico.linha(linha));
    }
}
