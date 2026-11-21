package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Acao;
import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Cenários montados à mão, sem inimigos, para o turno não depender do sorteio.
 * Fácil começa com 30 pontos; cada movimento custa 1; Professor vale 10.
 */
class GameSessionTest {

    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-11-21T12:00:00Z"), ZoneOffset.UTC);

    private static GameSession sessao(Missao missao) {
        return new GameSession("Teste", Dificuldade.FACIL, missao, new Random(0), RELOGIO);
    }

    /** Dois passageiros abaixo da base, em (0,1) e (0,2). */
    private static Missao missaoComDoisPassageiros() {
        Missao missao = new Missao(2, new Nave(5));
        missao.adicionar(new Professor("Dr. Um", new Posicao(0, 1)));
        missao.adicionar(new Professor("Dr. Dois", new Posicao(0, 2)));
        return missao;
    }

    @Test
    void embarqueSomaOBonusETiraDoMapa() {
        GameSession sessao = sessao(missaoComDoisPassageiros());

        sessao.jogar(Acao.BAIXO);
        ResultadoTurno resultado = sessao.jogar(Acao.EMBARCAR);

        assertEquals(39, sessao.pontos());
        assertEquals(1, sessao.aBordo());
        assertEquals(1, sessao.restantes());
        assertEquals("Passageiro Dr. Um embarcado com sucesso! +10 pontos!", resultado.mensagens().get(0));
    }

    @Test
    void embarcarSemPassageiroGastaOTurnoSemMudarPontos() {
        GameSession sessao = sessao(missaoComDoisPassageiros());

        ResultadoTurno resultado = sessao.jogar(Acao.EMBARCAR);

        assertEquals(30, sessao.pontos());
        assertEquals("Nenhum passageiro nesta posição.", resultado.mensagens().get(0));
    }

    @Test
    void desembarqueNaBaseLiberaACapacidadeEContaEntregue() {
        GameSession sessao = sessao(missaoComDoisPassageiros());

        sessao.jogar(Acao.BAIXO);
        sessao.jogar(Acao.EMBARCAR);
        ResultadoTurno resultado = sessao.jogar(Acao.CIMA);

        assertEquals(0, sessao.aBordo());
        assertEquals(1, sessao.entregues());
        assertEquals(1, sessao.restantes());
        assertEquals(sessao.total(), sessao.aBordo() + sessao.entregues() + sessao.restantes());
        assertFalse(sessao.acabou());
        assertEquals("Passageiros entregues na base: 1. Total entregues: 1/2.", resultado.mensagens().get(0));
    }

    @Test
    void colisaoTiraUmaVidaEOMovimentoTiraOPonto() {
        Missao missao = missaoComDoisPassageiros();
        missao.adicionar(new Asteroide(new Posicao(1, 0)));
        GameSession sessao = sessao(missao);

        sessao.jogar(Acao.DIREITA);
        assertEquals(2, sessao.vidas());
        assertEquals(29, sessao.pontos());

        sessao.jogar(Acao.EMBARCAR);
        assertEquals(1, sessao.vidas(), "parado no perigo, perde outra vida");
    }

    @Test
    void vitoriaSoQuandoTodosForemEntregues() {
        GameSession sessao = sessao(missaoComDoisPassageiros());

        sessao.jogar(Acao.BAIXO);
        sessao.jogar(Acao.EMBARCAR);
        sessao.jogar(Acao.BAIXO);
        sessao.jogar(Acao.EMBARCAR);
        assertFalse(sessao.acabou(), "todos a bordo, mas fora da base");

        sessao.jogar(Acao.CIMA);
        sessao.jogar(Acao.CIMA);

        assertTrue(sessao.venceu());
        assertTrue(sessao.acabou());
        assertEquals(2, sessao.entregues());
        assertEquals(46, sessao.pontos());
    }

    @Test
    void sairEncerraSemVitoriaEComandoInvalidoNaoGastaTurno() {
        GameSession sessao = sessao(missaoComDoisPassageiros());

        sessao.jogar(Acao.INVALIDA);
        assertEquals(30, sessao.pontos());
        assertFalse(sessao.acabou());

        sessao.jogar(Acao.SAIR);
        assertTrue(sessao.acabou());
        assertFalse(sessao.venceu());
    }
}
