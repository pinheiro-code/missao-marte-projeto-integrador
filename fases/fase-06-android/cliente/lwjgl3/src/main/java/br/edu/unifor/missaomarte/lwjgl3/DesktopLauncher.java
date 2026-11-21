package br.edu.unifor.missaomarte.lwjgl3;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.gdx.MissaoMarteGame;
import br.edu.unifor.missaomarte.gdx.Plataforma;
import br.edu.unifor.missaomarte.infrastructure.FileRankingRepository;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Random;

/** Composition root do desktop: monta as peças e abre a janela. */
public class DesktopLauncher {

    static final int LARGURA_PADRAO = 800;
    static final int ALTURA_PADRAO = 600;

    public static void main(String[] args) {
        Random random = criarRandom(System.getProperty("missaomarte.semente"));
        // -Dmissaomarte.semaudio=true: para máquina sem dispositivo de som
        boolean semAudio = Boolean.parseBoolean(System.getProperty("missaomarte.semaudio"));
        // -Dmissaomarte.toque=true: botões na tela e o mouse fazendo o papel do dedo, para testar sem celular
        boolean toque = Boolean.parseBoolean(System.getProperty("missaomarte.toque"));
        int[] janela = tamanhoDaJanela(System.getProperty("missaomarte.janela"));
        Clock clock = Clock.systemDefaultZone();
        RankingService ranking = new RankingService(new FileRankingRepository(Path.of("ranking.json")), clock);
        // -Dmissaomarte.servidor=http://host:porta é só o padrão: o salvo no jogo ganha; o jogo valida o valor
        MissaoMarteGame jogo = new MissaoMarteGame(new MissaoFactory(random), ranking, random, clock, !semAudio,
                new Plataforma(toque, false), System.getProperty("missaomarte.servidor"));

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Missão Marte");
        config.setWindowedMode(janela[0], janela[1]);
        config.useVsync(true);
        config.setForegroundFPS(60);
        config.disableAudio(semAudio);
        new Lwjgl3Application(jogo, config);
    }

    /**
     * -Dmissaomarte.janela=LxA (ex.: 1200x540 paisagem, 540x960 retrato) para simular telas de celular.
     * Ausente ou inválido vale 800x600.
     */
    static int[] tamanhoDaJanela(String valor) {
        int[] padrao = {LARGURA_PADRAO, ALTURA_PADRAO};
        if (valor == null || !valor.trim().matches("[0-9]{3,4}x[0-9]{3,4}")) {
            return padrao;
        }
        String[] partes = valor.trim().split("x");
        int largura = Integer.parseInt(partes[0]);
        int altura = Integer.parseInt(partes[1]);
        if (largura < 320 || altura < 240) {
            return padrao;
        }
        return new int[] {largura, altura};
    }

    /** O build.gradle repassa a propriedade vazia quando ninguém passou -D; vazia vale como ausente. */
    private static Random criarRandom(String semente) {
        if (semente == null || semente.isBlank()) {
            return new Random();
        }
        return new Random(Long.parseLong(semente.trim()));
    }
}
