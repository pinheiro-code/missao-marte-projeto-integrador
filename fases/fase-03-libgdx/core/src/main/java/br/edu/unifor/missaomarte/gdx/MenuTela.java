package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import java.io.UncheckedIOException;

/** Menu principal: 1 joga, 2 mostra o ranking, 3 limpa o ranking, 4 sai. */
public class MenuTela extends TelaBase {

    private String aviso = "";

    public MenuTela(MissaoMarteGame jogo) {
        super(jogo);
    }

    @Override
    protected void desenhar(float delta) {
        batch().begin();
        texto("MISSÃO MARTE UNIFOR", 300, 470, Color.ORANGE);
        texto("Pilote sua nave, salve os passageiros e desvie dos perigos!", 200, 430, Color.LIGHT_GRAY);
        texto("1  Jogar", 330, 360, Color.WHITE);
        texto("2  Ver ranking", 330, 330, Color.WHITE);
        texto("3  Limpar ranking", 330, 300, Color.WHITE);
        texto("4  Sair", 330, 270, Color.WHITE);
        texto(aviso, 330, 210, Color.YELLOW);
        batch().end();
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        switch (tecla) {
            case Keys.NUM_1, Keys.NUMPAD_1 -> jogo.trocarTela(new NovaPartidaTela(jogo));
            case Keys.NUM_2, Keys.NUMPAD_2 -> jogo.trocarTela(new RankingTela(jogo, null));
            case Keys.NUM_3, Keys.NUMPAD_3 -> limparRanking();
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
