package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
import br.edu.unifor.missaomarte.application.servidor.ServidorDoJogo;
import br.edu.unifor.missaomarte.application.servidor.Sessao;
import br.edu.unifor.missaomarte.gdx.rede.ServidorHttp;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.time.Clock;
import java.util.Random;
import java.util.function.Supplier;

/**
 * O jogo: cria uma vez os recursos que as telas dividem (inclusive imagens e sons) e troca de tela.
 * Fábrica, ranking, sorteio, relógio, a escolha do áudio e o endereço do servidor chegam prontos do launcher.
 * É o único dono da Sessao: com ela o jogo está no modo logado; sem ela, no modo convidado.
 */
public class MissaoMarteGame extends Game {

    public static final float LARGURA = 800;
    public static final float ALTURA = 600;
    private static final String TAG = "MissaoMarte";

    private final MissaoFactory fabrica;
    private final RankingService ranking;
    private final Random random;
    private final Clock clock;
    private final boolean comSom;
    private final Supplier<String> enderecoDoServidor;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer formas;
    private BitmapFont fonte;
    private Recursos recursos;
    private TocadorDeSom tocador;
    private ServidorDoJogo servidor;
    private Sessao sessao;

    public MissaoMarteGame(MissaoFactory fabrica, RankingService ranking, Random random, Clock clock, boolean comSom,
                           Supplier<String> enderecoDoServidor) {
        this.fabrica = fabrica;
        this.ranking = ranking;
        this.random = random;
        this.clock = clock;
        this.comSom = comSom;
        this.enderecoDoServidor = enderecoDoServidor;
    }

    @Override
    public void create() {
        Gdx.app.log(TAG, "create()" + (comSom ? "" : " sem áudio"));
        camera = new OrthographicCamera();
        viewport = new FitViewport(LARGURA, ALTURA, camera);
        batch = new SpriteBatch();
        formas = new ShapeRenderer();
        // a fonte padrão do libGDX já tem os acentos do português
        fonte = new BitmapFont();
        recursos = new Recursos(comSom);
        recursos.carregar();
        tocador = new TocadorDeSom(recursos, comSom);
        // Gdx.net e Gdx.app só existem depois que a aplicação abriu, por isso o adaptador nasce aqui
        servidor = new ServidorHttp(Gdx.net, Gdx.app::postRunnable, enderecoDoServidor);
        trocarTela(new MenuTela(this));
    }

    /**
     * O Game do libGDX só chama hide() na tela antiga; aqui ela também recebe dispose(),
     * para o ciclo de vida de cada tela fechar no log. Tela não guarda recurso pesado.
     */
    public void trocarTela(Screen nova) {
        Screen antiga = getScreen();
        setScreen(nova);
        if (antiga != null) {
            antiga.dispose();
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log(TAG, "dispose()");
        Screen atual = getScreen();
        super.dispose();
        if (atual != null) {
            atual.dispose();
        }
        batch.dispose();
        formas.dispose();
        fonte.dispose();
        recursos.dispose();
    }

    public void entrar(Sessao nova) {
        sessao = nova;
        Gdx.app.log(TAG, "entrou como " + nova.nome());
    }

    public void sairDaConta() {
        sessao = null;
        Gdx.app.log(TAG, "saiu da conta");
    }

    /** 401 numa rota protegida: descarta a sessão e volta ao menu do convidado com o aviso. */
    public void sessaoExpirada() {
        sairDaConta();
        trocarTela(new MenuTela(this, "Sessão expirada. Entre de novo."));
    }

    public boolean logado() {
        return sessao != null;
    }

    /** null no modo convidado. */
    public Sessao sessao() {
        return sessao;
    }

    public ServidorDoJogo servidor() {
        return servidor;
    }

    public MissaoFactory fabrica() {
        return fabrica;
    }

    public RankingService ranking() {
        return ranking;
    }

    public Random random() {
        return random;
    }

    public Clock clock() {
        return clock;
    }

    public OrthographicCamera camera() {
        return camera;
    }

    public FitViewport viewport() {
        return viewport;
    }

    public SpriteBatch batch() {
        return batch;
    }

    public ShapeRenderer formas() {
        return formas;
    }

    public BitmapFont fonte() {
        return fonte;
    }

    public Recursos recursos() {
        return recursos;
    }

    public TocadorDeSom tocador() {
        return tocador;
    }
}
