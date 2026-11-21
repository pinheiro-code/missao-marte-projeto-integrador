package br.edu.unifor.missaomarte.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.RankingEntry;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileRankingRepositoryTest {

    @TempDir
    Path pasta;

    @Test
    void gravaELeOMesmoRanking() {
        Path arquivo = pasta.resolve("ranking.json");
        FileRankingRepository repositorio = new FileRankingRepository(arquivo);
        List<RankingEntry> ranking = List.of(
                new RankingEntry("Ana \"Capitã\"", 70, Dificuldade.FACIL, 4, "2026-11-21 12:00:00", 35),
                new RankingEntry("Bia", 40, Dificuldade.DIFICIL, 5, "2026-11-21 12:05:00", 80));

        repositorio.salvar(ranking);

        assertTrue(Files.exists(arquivo));
        assertEquals(ranking, new FileRankingRepository(arquivo).carregar());
    }

    @Test
    void arquivoAusenteViraRankingVazio() {
        FileRankingRepository repositorio = new FileRankingRepository(pasta.resolve("nao-existe.json"));

        assertTrue(repositorio.carregar().isEmpty());
    }

    @Test
    void limparApagaOArquivo() {
        Path arquivo = pasta.resolve("ranking.json");
        FileRankingRepository repositorio = new FileRankingRepository(arquivo);
        repositorio.salvar(List.of(new RankingEntry("Ana", 10, Dificuldade.MEDIO, 1, "2026-11-21 12:00:00", 5)));

        repositorio.limpar();

        assertFalse(Files.exists(arquivo));
        assertTrue(repositorio.carregar().isEmpty());
    }
}
