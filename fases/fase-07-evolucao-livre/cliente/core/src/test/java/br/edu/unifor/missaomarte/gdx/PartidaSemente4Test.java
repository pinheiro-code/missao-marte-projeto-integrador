package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.domain.Acao;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import com.badlogic.gdx.Input.Keys;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Compatibilidade da fase 7 com a fase 6: a sequência da semente 4 do enunciado (F7, seção 7), jogada como
 * na tela (cada letra pelo TecladoParaAcao, um único Random(4) para a fábrica e para a sessão, mapa 5, fácil).
 * Os números foram medidos na cópia da fase 6, antes de qualquer código da melhoria. A sequência foi feita para
 * o mapa do professor; no mapa da equipe ela não vence (2 de 4 entregues), e é esse resultado que fica protegido.
 */
class PartidaSemente4Test {

    private static final String SEQUENCIA = "wwdwdwewedsdseaasasasasasasedwdwdw";
    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-11-21T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void sequenciaDaSemente4DaOMesmoResultadoDaFase6() {
        Random random = new Random(4);
        Missao missao = new MissaoFactory(random).criar(Dificuldade.FACIL, NovaPartidaTela.TAMANHO_MAPA);
        GameSession sessao = new GameSession("Piloto", Dificuldade.FACIL, missao, random, RELOGIO);
        TecladoParaAcao teclado = new TecladoParaAcao();

        for (char letra : SEQUENCIA.toCharArray()) {
            Acao acao = teclado.converter(Keys.valueOf(String.valueOf(Character.toUpperCase(letra))));
            sessao.jogar(acao);
        }

        assertFalse(sessao.acabou());
        assertEquals(2, sessao.entregues());
        assertEquals(4, sessao.total());
        assertEquals(25, sessao.pontos());
        assertEquals(30, sessao.movimentos());
        assertEquals(2, sessao.vidas());
        assertEquals(0, sessao.aBordo());
    }
}
