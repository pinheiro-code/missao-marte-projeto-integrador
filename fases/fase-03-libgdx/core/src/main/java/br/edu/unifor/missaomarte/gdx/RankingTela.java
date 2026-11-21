package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.RankingEntry;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import java.util.List;

/** Top 5 e, quando vem de uma vitória, o resumo da partida. Enter ou Esc volta ao menu. */
public class RankingTela extends TelaBase {

    private final List<RankingEntry> ranking;
    private final List<String> resumo;

    /** resumo é null quando a tela vem do menu. */
    public RankingTela(MissaoMarteGame jogo, List<String> resumo) {
        super(jogo);
        this.ranking = jogo.ranking().listar();
        this.resumo = resumo;
    }

    @Override
    protected void desenhar(float delta) {
        batch().begin();
        float y = 560;
        if (resumo != null) {
            texto("RESUMO DA PARTIDA", 40, y, Color.ORANGE);
            y -= 26;
            for (String linha : resumo) {
                texto(linha, 40, y, Color.WHITE);
                y -= 22;
            }
            y -= 20;
        }
        texto("RANKING TOP 5 PILOTOS", 40, y, Color.ORANGE);
        y -= 30;
        if (ranking.isEmpty()) {
            texto("Nenhum registro encontrado. Seja o primeiro a jogar!", 40, y, Color.LIGHT_GRAY);
        }
        int posicao = 1;
        for (RankingEntry e : ranking) {
            texto(String.format("%d. %s - %d pts | %s | coletados %d | %d s | %s", posicao++, e.name(), e.score(),
                    e.dificuldade(), e.passageirosColetados(), e.tempoJogo(), e.dataHora()), 40, y, Color.WHITE);
            y -= 24;
        }
        texto("Enter ou Esc: voltar ao menu", 40, 60, Color.GRAY);
        batch().end();
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        if (tecla == Keys.ENTER || tecla == Keys.NUMPAD_ENTER || tecla == Keys.ESCAPE) {
            jogo.trocarTela(new MenuTela(jogo));
            return true;
        }
        return false;
    }
}
