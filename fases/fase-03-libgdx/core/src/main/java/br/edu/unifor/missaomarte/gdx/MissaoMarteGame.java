package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.MissaoFactory;
import br.edu.unifor.missaomarte.application.RankingService;
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

/**
 * O jogo: cria uma vez os recursos que as telas dividem e troca de tela.
 * Fábrica, ranking, sorteio e relógio chegam prontos do launcher.
 */
public class MissaoMarteGame extends Game {

    public static final float LARGURA = 800;
    public static final float ALTURA = 600;
    private static final String TAG = "MissaoMarte";

    private final MissaoFactory fabrica;
    private final RankingService ranking;
    private final Random random;
    private final Clock clock;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer formas;
    private BitmapFont fonte;

    public MissaoMarteGame(MissaoFactory fabrica, RankingService ranking, Random random, Clock clock) {
        this.fabrica = fabrica;
        this.ranking = ranking;
        this.random = random;
        this.clock = clock;
    }

    @Override
    public void create() {
        Gdx.app.log(TAG, "create()");
        camera = new OrthographicCamera();
        viewport = new FitViewport(LARGURA, ALTURA, camera);
        batch = new SpriteBatch();
        formas = new ShapeRenderer();
        fonte = new BitmapFont();
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
}
