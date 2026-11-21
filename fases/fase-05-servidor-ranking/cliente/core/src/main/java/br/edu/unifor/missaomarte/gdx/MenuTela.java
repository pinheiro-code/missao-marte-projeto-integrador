package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import java.io.UncheckedIOException;

/** Menu principal com fundo, nave animada e música: 1 joga, 2 ranking, 3 limpa o ranking, 4 sai. */
public class MenuTela extends TelaBase {

    private String aviso = "";
    private float tempo;

    public MenuTela(MissaoMarteGame jogo) {
        super(jogo);
    }

    /** Toca a música ao entrar; depois de uma vitória ou derrota, é aqui que ela retoma. */
    @Override
    public void show() {
        super.show();
        jogo.tocador().tocarMusica();
    }

    @Override
    protected void desenhar(float delta) {
        tempo += delta;
        batch().begin();
        desenharFundo();
        // nave subindo e descendo devagar, com o propulsor em laço
        float y = 470 + 8 * (float) Math.sin(tempo * 2);
        batch().draw(recursos().quadroEmLaco(Imagem.PROPULSOR, tempo), 368, y, 64, 64);
        batch().draw(recursos().quadro(Imagem.NAVE, 0), 368, y, 64, 64);
        texto("MISSÃO MARTE UNIFOR", 318, 440, Color.ORANGE);
        texto("Pilote sua nave, salve os passageiros e desvie dos perigos!", 200, 410, Color.LIGHT_GRAY);
        texto("1  Jogar", 340, 350, Color.WHITE);
        texto("2  Ver ranking", 340, 320, Color.WHITE);
        texto("3  Limpar ranking", 340, 290, Color.WHITE);
        texto("4  Sair", 340, 260, Color.WHITE);
        texto(aviso, 340, 210, Color.YELLOW);
        texto(jogo.tocador().estado(), 16, 24, Color.GRAY);
        batch().end();
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        switch (tecla) {
            case Keys.NUM_1, Keys.NUMPAD_1 -> {
                jogo.tocador().tocar(Som.SELECIONAR);
                jogo.trocarTela(new NovaPartidaTela(jogo));
            }
            case Keys.NUM_2, Keys.NUMPAD_2 -> {
                jogo.tocador().tocar(Som.SELECIONAR);
                jogo.trocarTela(new RankingTela(jogo, null));
            }
            case Keys.NUM_3, Keys.NUMPAD_3 -> {
                jogo.tocador().tocar(Som.SELECIONAR);
                limparRanking();
            }
            case Keys.NUM_4, Keys.NUMPAD_4 -> Gdx.app.exit();
            default -> {
                return false;
            }
        }
        return true;
    }

    private void limparRanking() {
        try {
            jogo.ranking().limpar();
            aviso = "Ranking limpo.";
            Gdx.app.log("Menu", "ranking limpo");
        } catch (UncheckedIOException e) {
            aviso = "Erro ao limpar o ranking: " + e.getCause().getMessage();
        }
    }
}
