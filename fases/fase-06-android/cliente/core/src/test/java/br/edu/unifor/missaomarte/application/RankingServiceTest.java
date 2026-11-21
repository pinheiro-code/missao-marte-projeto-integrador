package br.edu.unifor.missaomarte.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.domain.Dificuldade;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Regras do Top 5 com o ranking guardado em memória, sem arquivo. */
class RankingServiceTest {

    private static final Clock RELOGIO = Clock.fixed(Instant.parse("2026-11-21T12:00:00Z"), ZoneOffset.UTC);

    private RankingEmMemoria repositorio;
    private RankingService servico;

    private static final class RankingEmMemoria implements RankingRepository {
        private List<RankingEntry> guardado = new ArrayList<>();

        @Override
        public List<RankingEntry> carregar() {
            return new ArrayList<>(guardado);
        }

        @Override
        public void salvar(List<RankingEntry> ranking) {
            guardado = new ArrayList<>(ranking);
        }

        @Override
        public void limpar() {
            guardado.clear();
        }
    }

    @BeforeEach
    void preparar() {
        repositorio = new RankingEmMemoria();
        servico = new RankingService(repositorio, RELOGIO);
    }

    private boolean registrar(String piloto, int pontos) {
        return servico.registrar(piloto, pontos, Dificuldade.MEDIO, 3, 40);
    }

    private List<Integer> pontos() {
        return servico.listar().stream().map(RankingEntry::score).toList();
    }

    @Test
    void zeroOuNegativoNaoEntra() {
        assertFalse(registrar("Zero", 0));
        assertFalse(registrar("Negativo", -3));
        assertTrue(servico.listar().isEmpty());
    }

    @Test
    void listaEmOrdemDecrescente() {
        registrar("Ana", 20);
        registrar("Bia", 50);
        registrar("Caio", 35);

        assertEquals(List.of(50, 35, 20), pontos());
    }

    @Test
    void guardaNoMaximoCinco() {
        for (int i = 1; i <= 7; i++) {
            registrar("P" + i, i * 10);
        }

        assertEquals(List.of(70, 60, 50, 40, 30), pontos());
    }

    @Test
    void comAListaCheiaSoEntraMaiorQueOPior() {
        for (int i = 1; i <= 5; i++) {
            registrar("P" + i, i * 10);
        }

        assertFalse(registrar("Empate", 10), "igual ao pior não entra");
        assertTrue(registrar("Maior", 11));
        assertEquals(List.of(50, 40, 30, 20, 11), pontos());
    }

    @Test
    void limparEsvazia() {
        registrar("Ana", 20);

        servico.limpar();

        assertTrue(servico.listar().isEmpty());
    }

    @Test
    void gravaOsDadosDaPartidaComADataDoRelogio() {
        servico.registrar("Ana", 42, Dificuldade.DIFICIL, 5, 77);

        assertEquals(new RankingEntry("Ana", 42, Dificuldade.DIFICIL, 5, "2026-11-21 12:00:00", 77),
                servico.listar().get(0));
    }
}
