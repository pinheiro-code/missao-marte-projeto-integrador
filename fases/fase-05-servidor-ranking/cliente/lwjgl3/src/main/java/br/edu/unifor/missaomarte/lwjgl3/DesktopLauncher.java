package br.edu.unifor.missaomarte.lwjgl3;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.infrastructure.FileRankingRepository;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Random;

/** Composition root do desktop: monta as peças e abre a janela. */
public class DesktopLauncher {

    public static void main(String[] args) {
        Random random = criarRandom(System.getProperty("missaomarte.semente"));
        // -Dmissaomarte.semaudio=true: para máquina sem dispositivo de som
        boolean semAudio = Boolean.parseBoolean(System.getProperty("missaomarte.semaudio"));
        Clock clock = Clock.systemDefaultZone();
        RankingService ranking = new RankingService(new FileRankingRepository(Path.of("ranking.json")), clock);
        MissaoMarteGame jogo = new MissaoMarteGame(new MissaoFactory(random), ranking, random, clock, !semAudio);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Missão Marte");
        config.setWindowedMode(800, 600);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.disableAudio(semAudio);
        new Lwjgl3Application(jogo, config);
    }

    /** O build.gradle repassa a propriedade vazia quando ninguém passou -D; vazia vale como ausente. */
    private static Random criarRandom(String semente) {
        if (semente == null || semente.isBlank()) {
            return new Random();
        }
        return new Random(Long.parseLong(semente.trim()));
    }
}
