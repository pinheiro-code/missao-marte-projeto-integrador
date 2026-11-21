package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.RankingEntry;
import br.edu.unifor.missaomarte.application.servidor.LinhaDoRanking;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Ranking e, quando vem de uma vitória, o resumo da partida. Enter ou Esc volta ao menu.
 * Convidado: Top 5 local, lido na hora, como na fase 4. Logado: Top 10 global, pedido ao servidor,
 * com estados de carga, R recarrega e o aviso de se o jogador está no Top 10.
 */
public class RankingTela extends TelaBase {

    private final List<String> linhas = new ArrayList<>();
    private final List<String> resumo;
    private final boolean global;
    private boolean vazio;
    private EstadoDaCarga estado = EstadoDaCarga.PRONTO;
    private String erro;
    private boolean noTop10;

    /** resumo é null quando a tela vem do menu. */
    public RankingTela(MissaoMarteGame jogo, List<String> resumo) {
        super(jogo);
        this.resumo = resumo;
        this.global = jogo.logado();
        if (global) {
            carregarGlobal();
        } else {
            carregarLocal();
        }
    }

    private void carregarLocal() {
        List<RankingEntry> ranking = jogo.ranking().listar();
        vazio = ranking.isEmpty();
        int posicao = 1;
        for (RankingEntry e : ranking) {
            linhas.add(String.format("%d. %s - %d pts | %s | coletados %d | %d s | %s", posicao++, e.name(),
                    e.score(), e.dificuldade(), e.passageirosColetados(), e.tempoJogo(), e.dataHora()));
        }
    }

    private void carregarGlobal() {
        estado = EstadoDaCarga.CARREGANDO;
        jogo.servidor().carregarRanking(jogo.sessao(), this::receber);
    }

    private void receber(Resposta<List<LinhaDoRanking>> resposta) {
        if (jogo.getScreen() != this) {
            return;
        }
        if (resposta.naoAutorizado()) {
            jogo.sessaoExpirada();
            return;
        }
        if (!resposta.ok()) {
            erro = resposta.erro();
            estado = EstadoDaCarga.ERRO;
            return;
        }
        linhas.clear();
        noTop10 = false;
        for (LinhaDoRanking linha : resposta.valor()) {
            linhas.add(FormatoDoHistorico.linha(linha));
            noTop10 |= linha.nome().equalsIgnoreCase(jogo.sessao().nome());
        }
        vazio = linhas.isEmpty();
        estado = EstadoDaCarga.PRONTO;
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
        texto(global ? "RANKING GLOBAL TOP 10" : "RANKING TOP 5 PILOTOS", 40, y, Color.ORANGE);
        y -= 30;
        if (estado == EstadoDaCarga.CARREGANDO) {
            texto("Carregando o ranking do servidor...", 40, y, Color.SKY);
        } else if (estado == EstadoDaCarga.ERRO) {
            texto(erro, 40, y, Color.YELLOW);
        } else {
            y = desenharLinhas(y);
        }
        String rodape = global ? "R recarrega     Enter ou Esc: voltar ao menu     " : "Enter ou Esc: voltar ao menu     ";
        texto(rodape + jogo.tocador().estado(), 40, 60, Color.LIGHT_GRAY);
        batch().end();
    }

    private float desenharLinhas(float y) {
        if (vazio) {
            texto("Nenhum registro encontrado. Seja o primeiro a jogar!", 40, y, Color.LIGHT_GRAY);
            y -= 24;
        }
        for (String linha : linhas) {
            texto(linha, 40, y, Color.WHITE);
            y -= global ? 22 : 24;
        }
        if (global) {
            texto(noTop10 ? "Você está no Top 10!" : "Você ainda não está no Top 10.", 40, y - 8,
                    noTop10 ? Color.GREEN : Color.LIGHT_GRAY);
        }
        return y;
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        if (global && tecla == Keys.R && estado != EstadoDaCarga.CARREGANDO) {
            carregarGlobal();
            return true;
        }
        if (tecla == Keys.ENTER || tecla == Keys.NUMPAD_ENTER || tecla == Keys.ESCAPE) {
            jogo.tocador().tocar(Som.SELECIONAR);
            jogo.trocarTela(new MenuTela(jogo));
            return true;
        }
        return false;
    }
}
