package br.edu.unifor.missaomarte;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.console.ConsoleGame;
import br.edu.unifor.missaomarte.console.MapaRenderer;
import br.edu.unifor.missaomarte.infrastructure.FileRankingRepository;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Random;
import java.util.Scanner;

/** Só monta as peças e inicia o jogo. */
public class Main {

    public static void main(String[] args) {
        String semente = System.getProperty("missaomarte.semente");
        Random random = semente == null ? new Random() : new Random(Long.parseLong(semente));
        Clock clock = Clock.systemDefaultZone();
        RankingService ranking = new RankingService(new FileRankingRepository(Path.of("ranking.json")), clock);

        new ConsoleGame(new Scanner(System.in), new MissaoFactory(random), ranking, new MapaRenderer(), random, clock)
                .iniciar();
    }
}
