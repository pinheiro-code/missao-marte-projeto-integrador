package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.GameSession;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import java.util.List;

/**
 * HUD desenhado depois do mundo. Só lê a GameSession: barra superior (vidas, pontos, tempo, som),
 * painel lateral (piloto, dificuldade, vagas, entregas, restantes, movimentos, mensagens) e rodapé.
 */
public class Hud {

    private static final float PAINEL_X = 560;
    private static final float PAINEL_LARGURA = 224;
    private static final float BARRA_Y = 460;
    private static final float ICONE = 16;

    private final Recursos recursos;
    private final BitmapFont fonte;
    /** As vidas do começo da partida, para desenhar os corações vazios. */
    private final int vidasIniciais;

    public Hud(Recursos recursos, BitmapFont fonte, GameSession sessao) {
        this.recursos = recursos;
        this.fonte = fonte;
        this.vidasIniciais = sessao.vidas();
    }

    public void desenhar(SpriteBatch batch, ShapeRenderer formas, GameSession sessao, List<String> mensagens,
                         String estadoDoSom) {
        desenharPaineis(formas, sessao);
        batch.begin();
        barraSuperior(batch, sessao, estadoDoSom);
        painelLateral(batch, sessao, mensagens);
        texto(batch, "W/A/S/D ou setas: mover (segure para repetir)   E: embarcar   Q/Esc: abortar   M: som",
                16, 20, Color.LIGHT_GRAY);
        batch.end();
    }

    /** Painéis translúcidos: o ShapeRenderer só mistura a transparência com o blending ligado. */
    private void desenharPaineis(ShapeRenderer formas, GameSession sessao) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas.begin(ShapeType.Filled);
        formas.setColor(0, 0, 0, 0.6f);
        formas.rect(0, 568, MissaoMarteGame.LARGURA, 32);
        formas.rect(0, 0, MissaoMarteGame.LARGURA, 28);
        formas.setColor(0.06f, 0.03f, 0.09f, 0.75f);
        formas.rect(PAINEL_X - 8, 32, PAINEL_LARGURA + 16, 532);
        // barra de progresso das entregas
        formas.setColor(0.3f, 0.3f, 0.35f, 0.9f);
        formas.rect(PAINEL_X, BARRA_Y, PAINEL_LARGURA, 8);
        float feito = sessao.total() == 0 ? 1 : (float) sessao.entregues() / sessao.total();
        formas.setColor(0.25f, 0.85f, 0.35f, 1);
        formas.rect(PAINEL_X, BARRA_Y, PAINEL_LARGURA * feito, 8);
        formas.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void barraSuperior(SpriteBatch batch, GameSession sessao, String estadoDoSom) {
        for (int i = 0; i < vidasIniciais; i++) {
            Imagem icone = i < sessao.vidas() ? Imagem.VIDA : Imagem.VIDA_VAZIA;
            batch.draw(recursos.quadro(icone, 0), 16 + i * 22, 576, ICONE, ICONE);
        }
        texto(batch, "Pontos: " + sessao.pontos(), 110, 590, Color.WHITE);
        texto(batch, "Tempo: " + sessao.duracaoSegundos() + " s", 240, 590, Color.WHITE);
        texto(batch, estadoDoSom, 600, 590, Color.LIGHT_GRAY);
    }

    private void painelLateral(SpriteBatch batch, GameSession sessao, List<String> mensagens) {
        texto(batch, "Piloto: " + sessao.piloto(), PAINEL_X, 552, Color.WHITE);
        texto(batch, "Dificuldade: " + sessao.dificuldade(), PAINEL_X, 530, Color.WHITE);
        texto(batch, "Vagas:", PAINEL_X, 508, Color.WHITE);
        for (int i = 0; i < sessao.capacidade(); i++) {
            Imagem icone = i < sessao.aBordo() ? Imagem.VAGA : Imagem.VAGA_LIVRE;
            batch.draw(recursos.quadro(icone, 0), PAINEL_X + 56 + i * 20, 494, ICONE, ICONE);
        }
        texto(batch, "Entregas: " + sessao.entregues() + "/" + sessao.total(), PAINEL_X, 490, Color.WHITE);
        texto(batch, "Restantes no mapa: " + sessao.restantes(), PAINEL_X, 444, Color.WHITE);
        texto(batch, "Movimentos: " + sessao.movimentos(), PAINEL_X, 422, Color.WHITE);
        texto(batch, "Último turno:", PAINEL_X, 392, Color.ORANGE);
        float y = 370;
        fonte.setColor(Color.LIGHT_GRAY);
        for (String mensagem : mensagens) {
            GlyphLayout bloco = fonte.draw(batch, mensagem, PAINEL_X, y, PAINEL_LARGURA, Align.left, true);
            y -= bloco.height + 10;
        }
    }

    private void texto(SpriteBatch batch, String texto, float x, float y, Color cor) {
        fonte.setColor(cor);
        fonte.draw(batch, texto, x, y);
    }
}
