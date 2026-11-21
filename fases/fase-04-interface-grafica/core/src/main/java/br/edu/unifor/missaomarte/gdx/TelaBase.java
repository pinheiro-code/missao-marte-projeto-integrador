package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * O que toda tela faz igual: log do ciclo de vida, viewport, projeção, teclado e o atalho M do som.
 * A tela concreta só diz como desenha e o que faz com cada tecla.
 */
public abstract class TelaBase implements Screen {

    private static final String TAG = "Tela";
    private static final Color FUNDO = new Color(0.08f, 0.04f, 0.04f, 1);

    protected final MissaoMarteGame jogo;
    private final InputProcessor entrada = new InputAdapter() {
        @Override
        public boolean keyDown(int tecla) {
            // atalho central do som: vale em toda tela que não digita texto
            if (tecla == Keys.M && aceitaAtalhoDeSom()) {
                jogo.tocador().alternarMudo();
                Gdx.app.log(TAG, jogo.tocador().estado());
                return true;
            }
            return teclaApertada(tecla);
        }

        @Override
        public boolean keyUp(int tecla) {
            return teclaSolta(tecla);
        }

        @Override
        public boolean keyTyped(char caractere) {
            return caractereDigitado(caractere);
        }
    };

    protected TelaBase(MissaoMarteGame jogo) {
        this.jogo = jogo;
    }

    private void log(String evento) {
        Gdx.app.log(TAG, getClass().getSimpleName() + "." + evento);
    }

    @Override
    public void show() {
        log("show()");
        Gdx.input.setInputProcessor(entrada);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(FUNDO);
        jogo.viewport().apply();
        aplicarProjecao();
        desenhar(delta);
    }

    /** Projeção da câmera no batch e nas formas; chamar de novo depois de mexer na câmera. */
    protected void aplicarProjecao() {
        jogo.camera().update();
        jogo.batch().setProjectionMatrix(jogo.camera().combined);
        jogo.formas().setProjectionMatrix(jogo.camera().combined);
    }

    @Override
    public void resize(int largura, int altura) {
        log("resize(" + largura + ", " + altura + ")");
        jogo.viewport().update(largura, altura, true);
    }

    @Override
    public void pause() {
        log("pause()");
    }

    @Override
    public void resume() {
        log("resume()");
    }

    @Override
    public void hide() {
        log("hide()");
        if (Gdx.input.getInputProcessor() == entrada) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        log("dispose()");
    }

    /** Desenha o quadro. Batch e formas já estão com a projeção da câmera. */
    protected abstract void desenhar(float delta);

    protected boolean teclaApertada(int tecla) {
        return false;
    }

    protected boolean teclaSolta(int tecla) {
        return false;
    }

    protected boolean caractereDigitado(char caractere) {
        return false;
    }

    /** A tela de nome devolve falso: lá a letra M faz parte do nome. */
    protected boolean aceitaAtalhoDeSom() {
        return true;
    }

    // atalhos de desenho usados por todas as telas

    protected SpriteBatch batch() {
        return jogo.batch();
    }

    protected ShapeRenderer formas() {
        return jogo.formas();
    }

    protected BitmapFont fonte() {
        return jogo.fonte();
    }

    protected Recursos recursos() {
        return jogo.recursos();
    }

    /** Fundo de 800x600; chamar com o batch aberto. */
    protected void desenharFundo() {
        batch().draw(recursos().textura(Imagem.FUNDO), 0, 0, MissaoMarteGame.LARGURA, MissaoMarteGame.ALTURA);
    }

    /** Texto com o y no topo, como o BitmapFont desenha. */
    protected void texto(String texto, float x, float y, Color cor) {
        fonte().setColor(cor);
        fonte().draw(batch(), texto, x, y);
    }
}
