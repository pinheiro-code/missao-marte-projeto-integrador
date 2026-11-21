package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Collections;
import java.util.List;

/**
 * O que toda tela faz igual: log do ciclo de vida, viewport, projeção, teclado, o atalho M do som,
 * a tecla Voltar do Android e, no modo toque, os botões da tela (desenho e toque).
 * A tela concreta só diz como desenha, o que faz com cada tecla e, no modo toque, quais botões tem.
 */
public abstract class TelaBase implements Screen {

    private static final String TAG = "Tela";
    private static final Color FUNDO = new Color(0.08f, 0.04f, 0.04f, 1);
    private static final Color FUNDO_DO_BOTAO = new Color(0.16f, 0.12f, 0.26f, 0.85f);
    private static final Color BORDA_DO_BOTAO = new Color(0.95f, 0.6f, 0.25f, 1);

    protected final MissaoMarteGame jogo;
    private final Vector2 ponto = new Vector2();
    private final GlyphLayout medida = new GlyphLayout();
    private List<BotaoVirtual> botoes = Collections.emptyList();
    private float larguraDoMundo = LayoutDosControles.LARGURA_MINIMA;
    private float alturaDoMundo = LayoutDosControles.ALTURA_MINIMA;

    private final InputProcessor entrada = new InputAdapter() {
        @Override
        public boolean keyDown(int tecla) {
            // o Voltar do Android só chega aqui porque o show() o captura; sem isso o sistema fecha o app
            if (tecla == Keys.BACK) {
                voltar();
                return true;
            }
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

        @Override
        public boolean touchDown(int telaX, int telaY, int ponteiro, int botaoDoMouse) {
            if (!modoToque()) {
                return false;
            }
            BotaoVirtual tocado = botaoEm(telaX, telaY);
            if (tocado == null) {
                return false;
            }
            aoTocar(tocado.id(), ponteiro);
            return true;
        }

        @Override
        public boolean touchUp(int telaX, int telaY, int ponteiro, int botaoDoMouse) {
            if (modoToque()) {
                aoSoltar(ponteiro);
            }
            return false;
        }

        @Override
        public boolean touchDragged(int telaX, int telaY, int ponteiro) {
            if (modoToque()) {
                paraOMundo(telaX, telaY);
                aoArrastar(ponteiro, ponto.x, ponto.y);
            }
            return false;
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
        Gdx.input.setCatchKey(Keys.BACK, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(FUNDO);
        jogo.viewport().apply();
        aplicarProjecao();
        desenhar(delta);
        if (!botoes.isEmpty()) {
            desenharBotoes();
        }
    }

    /** Projeção da câmera no batch e nas formas; chamar de novo depois de mexer na câmera. */
    protected void aplicarProjecao() {
        jogo.camera().update();
        jogo.batch().setProjectionMatrix(jogo.camera().combined);
        jogo.formas().setProjectionMatrix(jogo.camera().combined);
    }

    /**
     * Com a câmera no padrão (centrada no mundo), o mundo vai de 0 até a largura e a altura visíveis:
     * o jogo fica em 0..800 x 0..600 e, no modo toque, a coluna de controles na sobra da direita.
     */
    @Override
    public void resize(int largura, int altura) {
        log("resize(" + largura + ", " + altura + ")");
        jogo.viewport().update(largura, altura, true);
        larguraDoMundo = jogo.viewport().getWorldWidth();
        alturaDoMundo = jogo.viewport().getWorldHeight();
        atualizarBotoes();
    }

    /** O app foi para segundo plano: a música pausa (e só volta se tocava antes). */
    @Override
    public void pause() {
        log("pause()");
        jogo.tocador().pausarPorSegundoPlano();
    }

    @Override
    public void resume() {
        log("resume()");
        jogo.tocador().retomarDoSegundoPlano();
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

    /** A tecla Voltar do Android. Padrão: o mesmo que o Esc da tela. */
    protected void voltar() {
        teclaApertada(Keys.ESCAPE);
    }

    // modo toque

    protected boolean modoToque() {
        return jogo.plataforma().modoToque();
    }

    /** Os botões da tela para o mundo visível; chamado no resize e pelo atualizarBotoes(). */
    protected List<BotaoVirtual> montarBotoes(float largura, float altura) {
        return Collections.emptyList();
    }

    /** Monta de novo os botões (a tela chama quando o estado muda e os botões mudam junto). */
    protected void atualizarBotoes() {
        botoes = modoToque() ? montarBotoes(larguraDoMundo, alturaDoMundo) : Collections.<BotaoVirtual>emptyList();
    }

    /** O toque caiu no botão id; o ponteiro é o dedo (no desktop, o mouse é o 0). */
    protected void aoTocar(String id, int ponteiro) {
    }

    protected void aoSoltar(int ponteiro) {
    }

    /** O dedo arrastou para (x, y) do mundo. */
    protected void aoArrastar(int ponteiro, float x, float y) {
    }

    /** O texto de cada botão; a tela troca quando o rótulo depende do estado (ex.: o nome digitado). */
    protected String rotulo(BotaoVirtual botao) {
        return botao.rotulo();
    }

    /** null quando a tela não tem o botão agora. */
    protected BotaoVirtual botao(String id) {
        for (BotaoVirtual b : botoes) {
            if (b.id().equals(id)) {
                return b;
            }
        }
        return null;
    }

    private BotaoVirtual botaoEm(int telaX, int telaY) {
        paraOMundo(telaX, telaY);
        for (BotaoVirtual b : botoes) {
            if (b.contem(ponto.x, ponto.y)) {
                return b;
            }
        }
        return null;
    }

    /** Da tela (pixels, y para baixo) para o mundo (y para cima), com as faixas do viewport. */
    private void paraOMundo(int telaX, int telaY) {
        ponto.set(telaX, telaY);
        jogo.viewport().unproject(ponto);
    }

    private void desenharBotoes() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas().begin(ShapeType.Filled);
        for (BotaoVirtual b : botoes) {
            formas().setColor(FUNDO_DO_BOTAO);
            formas().rect(b.x(), b.y(), b.largura(), b.altura());
            formas().setColor(Color.WHITE);
            desenharSeta(b);
        }
        formas().end();
        formas().begin(ShapeType.Line);
        formas().setColor(BORDA_DO_BOTAO);
        for (BotaoVirtual b : botoes) {
            formas().rect(b.x(), b.y(), b.largura(), b.altura());
        }
        formas().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch().begin();
        fonte().setColor(Color.WHITE);
        for (BotaoVirtual b : botoes) {
            String texto = rotulo(b);
            if (!ehSeta(texto)) {
                medida.setText(fonte(), texto);
                fonte().draw(batch(), texto, b.x() + (b.largura() - medida.width) / 2,
                        b.y() + (b.altura() + medida.height) / 2);
            }
        }
        batch().end();
    }

    private static boolean ehSeta(String rotulo) {
        return rotulo.equals("^") || rotulo.equals("v") || rotulo.equals("<") || rotulo.equals(">");
    }

    /** As setas viram triângulos, que leem melhor que as letras da fonte padrão. */
    private void desenharSeta(BotaoVirtual b) {
        float cx = b.x() + b.largura() / 2;
        float cy = b.y() + b.altura() / 2;
        float r = Math.min(b.largura(), b.altura()) / 4;
        switch (b.rotulo()) {
            case "^" -> formas().triangle(cx - r, cy - r, cx + r, cy - r, cx, cy + r);
            case "v" -> formas().triangle(cx - r, cy + r, cx + r, cy + r, cx, cy - r);
            case "<" -> formas().triangle(cx + r, cy - r, cx + r, cy + r, cx - r, cy);
            case ">" -> formas().triangle(cx - r, cy - r, cx - r, cy + r, cx + r, cy);
            default -> {
            }
        }
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
