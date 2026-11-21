package br.edu.unifor.missaomarte.application.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Acao;
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

class PartidaConcluidaTest {

    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-11-21T12:00:00Z"), ZoneOffset.UTC);

    private static GameSession sessao(Missao missao) {
        return new GameSession("Teste", Dificuldade.DIFICIL, missao, new Random(0), RELOGIO);
    }

    @Test
    void vitoriaLevaOsNumerosDaSessao() {
        // missão sem passageiro nasce vencida
        GameSession sessao = sessao(new Missao(2, new Nave(5)));

        PartidaConcluida partida = PartidaConcluida.de(sessao);

        assertTrue(partida.vitoria());
        assertEquals(Dificuldade.DIFICIL, partida.dificuldade());
        assertEquals(45, partida.pontuacao());
        assertEquals(0, partida.duracaoSegundos());
        assertEquals(0, partida.movimentos());
    }

    @Test
    void partidaQueNaoAcabouLancaExcecao() {
        Missao missao = new Missao(2, new Nave(5));
        missao.adicionar(new Professor("Dr. Um", new Posicao(0, 1)));
        GameSession sessao = sessao(missao);

        assertThrows(IllegalStateException.class, () -> PartidaConcluida.de(sessao));
    }

    @Test
    void partidaEncerradaSemVitoriaViraDerrota() {
        Missao missao = new Missao(2, new Nave(5));
        missao.adicionar(new Professor("Dr. Um", new Posicao(0, 1)));
        GameSession sessao = sessao(missao);
        sessao.jogar(Acao.SAIR);

        assertFalse(PartidaConcluida.de(sessao).vitoria());
    }
}
