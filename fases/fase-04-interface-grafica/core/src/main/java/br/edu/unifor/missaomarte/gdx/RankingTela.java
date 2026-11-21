package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.RankingEntry;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

/** Top 5 e, quando vem de uma vitória, o resumo da partida. Enter ou Esc volta ao menu. */
public class RankingTela extends TelaBase {

    private final List<String> linhas = new ArrayList<>();
    private final List<String> resumo;
    private final boolean vazio;

    /** resumo é null quando a tela vem do menu. */
    public RankingTela(MissaoMarteGame jogo, List<String> resumo) {
        super(jogo);
        this.resumo = resumo;
        List<RankingEntry> ranking = jogo.ranking().listar();
        vazio = ranking.isEmpty();
        int posicao = 1;
        for (RankingEntry e : ranking) {
            linhas.add(String.format("%d. %s - %d pts | %s | coletados %d | %d s | %s", posicao++, e.name(),
                    e.score(), e.dificuldade(), e.passageirosColetados(), e.tempoJogo(), e.dataHora()));
        }
    }

    @Override
    protected void desenhar(float delta) {
        batch().begin();
        desenharFundo();
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
        if (vazio) {
            texto("Nenhum registro encontrado. Seja o primeiro a jogar!", 40, y, Color.LIGHT_GRAY);
        }
        for (String linha : linhas) {
            texto(linha, 40, y, Color.WHITE);
            y -= 24;
        }
        texto("Enter ou Esc: voltar ao menu     " + jogo.tocador().estado(), 40, 60, Color.LIGHT_GRAY);
        batch().end();
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        if (tecla == Keys.ENTER || tecla == Keys.NUMPAD_ENTER || tecla == Keys.ESCAPE) {
            jogo.tocador().tocar(Som.SELECIONAR);
            jogo.trocarTela(new MenuTela(jogo));
            return true;
        }
        return false;
    }
}
