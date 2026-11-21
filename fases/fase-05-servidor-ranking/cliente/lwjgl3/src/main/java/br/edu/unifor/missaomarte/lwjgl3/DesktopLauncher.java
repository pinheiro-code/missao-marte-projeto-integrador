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

    private static final String SERVIDOR_PADRAO = "http://localhost:8080";

    public static void main(String[] args) {
        Random random = criarRandom(System.getProperty("missaomarte.semente"));
        // -Dmissaomarte.semaudio=true: para máquina sem dispositivo de som
        boolean semAudio = Boolean.parseBoolean(System.getProperty("missaomarte.semaudio"));
        Clock clock = Clock.systemDefaultZone();
        RankingService ranking = new RankingService(new FileRankingRepository(Path.of("ranking.json")), clock);
        // -Dmissaomarte.servidor=http://host:porta; vazio ou ausente vale o servidor local
        String endereco = enderecoDoServidor(System.getProperty("missaomarte.servidor"));
        MissaoMarteGame jogo = new MissaoMarteGame(new MissaoFactory(random), ranking, random, clock, !semAudio,
                () -> endereco);

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Missão Marte");
        config.setWindowedMode(800, 600);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.disableAudio(semAudio);
        new Lwjgl3Application(jogo, config);
    }

    private static String enderecoDoServidor(String valor) {
        if (valor == null || valor.isBlank()) {
            return SERVIDOR_PADRAO;
        }
        String endereco = valor.trim();
        // as rotas já começam com /: a barra final viraria //api
        return endereco.endsWith("/") ? endereco.substring(0, endereco.length() - 1) : endereco;
    }

    /** O build.gradle repassa a propriedade vazia quando ninguém passou -D; vazia vale como ausente. */
    private static Random criarRandom(String semente) {
        if (semente == null || semente.isBlank()) {
            return new Random();
        }
        return new Random(Long.parseLong(semente.trim()));
    }
}
