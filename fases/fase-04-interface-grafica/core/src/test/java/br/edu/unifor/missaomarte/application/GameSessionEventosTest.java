package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Acao;
import br.edu.unifor.missaomarte.domain.Asteroide;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Nave;
import br.edu.unifor.missaomarte.domain.Posicao;
import br.edu.unifor.missaomarte.domain.Professor;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Eventos do turno, em cenários sem inimigos (o turno não depende do sorteio).
 * Toda missão tem um passageiro longe, em (2,2): sem passageiro a partida já nasce vencida.
 */
class GameSessionEventosTest {

    /** Relógio que só anda quando o teste manda. */
    private static final class RelogioManual extends Clock {
        private Instant agora = Instant.parse("2026-11-21T12:00:00Z");

        void avancar(long segundos) {
            agora = agora.plus(Duration.ofSeconds(segundos));
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zona) {
            return this;
        }

        @Override
        public Instant instant() {
            return agora;
        }
    }

    private final RelogioManual relogio = new RelogioManual();

    private GameSession sessao(Missao missao) {
        return new GameSession("Teste", Dificuldade.FACIL, missao, new Random(0), relogio);
    }

    /** Passageiro logo abaixo da base, em (0,1), e outro longe, em (2,2). */
    private static Missao missao() {
        Missao missao = new Missao(2, new Nave(5));
        missao.adicionar(new Professor("Dr. Perto", new Posicao(0, 1)));
        missao.adicionar(new Professor("Dr. Longe", new Posicao(2, 2)));
        return missao;
    }

    @Test
    void movimentoGeraMovimento() {
        assertEquals(List.of(EventoJogo.MOVIMENTO), sessao(missao()).jogar(Acao.DIREITA).eventos());
    }

    @Test
    void naBordaOPontoECobradoMasNaoHaMovimento() {
        GameSession sessao = sessao(missao());
        sessao.jogar(Acao.CIMA);
        sessao.jogar(Acao.CIMA);

        ResultadoTurno resultado = sessao.jogar(Acao.CIMA);

        assertEquals(List.of(), resultado.eventos());
        assertEquals(27, sessao.pontos());
    }

    @Test
    void embarqueGeraEmbarque() {
        GameSession sessao = sessao(missao());
        sessao.jogar(Acao.BAIXO);

        assertEquals(List.of(EventoJogo.EMBARQUE), sessao.jogar(Acao.EMBARCAR).eventos());
    }

    @Test
    void embarcarSemPassageiroNaoGeraEvento() {
        assertEquals(List.of(), sessao(missao()).jogar(Acao.EMBARCAR).eventos());
    }

    @Test
    void voltarABaseComPassageiroGeraEntrega() {
        GameSession sessao = sessao(missao());
        sessao.jogar(Acao.BAIXO);
        sessao.jogar(Acao.EMBARCAR);

        assertEquals(List.of(EventoJogo.MOVIMENTO, EventoJogo.ENTREGA), sessao.jogar(Acao.CIMA).eventos());
    }

    @Test
    void colisaoGeraColisao() {
        Missao missao = missao();
        missao.adicionar(new Asteroide(new Posicao(1, 0)));

        assertEquals(List.of(EventoJogo.MOVIMENTO, EventoJogo.COLISAO), sessao(missao).jogar(Acao.DIREITA).eventos());
    }

    @Test
    void ultimaEntregaGeraVitoria() {
        Missao missao = new Missao(2, new Nave(5));
        missao.adicionar(new Professor("Dr. Unico", new Posicao(0, 1)));
        GameSession sessao = sessao(missao);
        sessao.jogar(Acao.BAIXO);
        sessao.jogar(Acao.EMBARCAR);

        ResultadoTurno resultado = sessao.jogar(Acao.CIMA);

        assertEquals(List.of(EventoJogo.MOVIMENTO, EventoJogo.ENTREGA, EventoJogo.VITORIA), resultado.eventos());
        assertTrue(sessao.venceu());
    }

    @Test
    void ultimaVidaGeraColisaoEDerrota() {
        Missao missao = missao();
        missao.adicionar(new Asteroide(new Posicao(1, 0)));
        GameSession sessao = sessao(missao);
        sessao.jogar(Acao.DIREITA);
        sessao.jogar(Acao.EMBARCAR);

        ResultadoTurno resultado = sessao.jogar(Acao.EMBARCAR);

        assertEquals(List.of(EventoJogo.COLISAO, EventoJogo.DERROTA), resultado.eventos());
        assertTrue(sessao.acabou());
    }

    @Test
    void comandoInvalidoEAbortarNaoGeramEventos() {
        GameSession sessao = sessao(missao());

        assertEquals(List.of(), sessao.jogar(Acao.INVALIDA).eventos());
        assertEquals(List.of(), sessao.jogar(Acao.SAIR).eventos());
        assertTrue(sessao.acabou());
    }

    @Test
    void duracaoParaQuandoAPartidaTermina() {
        Missao missao = new Missao(2, new Nave(5));
        missao.adicionar(new Professor("Dr. Unico", new Posicao(0, 1)));
        GameSession sessao = sessao(missao);
        relogio.avancar(4);
        sessao.jogar(Acao.BAIXO);
        sessao.jogar(Acao.EMBARCAR);
        relogio.avancar(6);
        sessao.jogar(Acao.CIMA);
        assertTrue(sessao.acabou());

        relogio.avancar(50);

        assertEquals(10, sessao.duracaoSegundos(), "o resumo mostra o mesmo tempo que foi gravado");
    }

    @Test
    void missaoSemPassageiroJaNasceVencida() {
        GameSession sessao = sessao(new Missao(2, new Nave(5)));

        assertTrue(sessao.acabou());
        assertTrue(sessao.venceu());
    }

    @Test
    void construtorAntigoDoResultadoTurnoContinuaValendo() {
        ResultadoTurno resultado = new ResultadoTurno(List.of("Comando inválido."));

        assertEquals(List.of("Comando inválido."), resultado.mensagens());
        assertEquals(List.of(), resultado.eventos());
    }
}
