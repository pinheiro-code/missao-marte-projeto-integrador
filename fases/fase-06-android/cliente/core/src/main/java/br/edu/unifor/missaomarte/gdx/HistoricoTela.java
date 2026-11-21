package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.PartidaRegistrada;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import java.util.List;

/**
 * As últimas 10 partidas do jogador logado, pedidas ao servidor. R recarrega, Enter ou Esc volta.
 * No modo toque, Recarregar e Voltar viram botões no canto de baixo.
 */
public class HistoricoTela extends TelaBase {

    private EstadoDaCarga estado;
    private List<PartidaRegistrada> partidas;
    private String erro;

    public HistoricoTela(MissaoMarteGame jogo) {
        super(jogo);
        carregar();
    }

    private void carregar() {
        estado = EstadoDaCarga.CARREGANDO;
        jogo.servidor().carregarHistorico(jogo.sessao(), this::receber);
    }

    private void receber(Resposta<List<PartidaRegistrada>> resposta) {
        if (jogo.getScreen() != this) {
            return;
        }
        if (resposta.naoAutorizado()) {
            jogo.sessaoExpirada();
        } else if (resposta.ok()) {
            partidas = resposta.valor();
            estado = EstadoDaCarga.PRONTO;
        } else {
            erro = resposta.erro();
            estado = EstadoDaCarga.ERRO;
        }
    }

    @Override
    protected void desenhar(float delta) {
        batch().begin();
        desenharFundo();
        texto("MEU HISTÓRICO: " + jogo.sessao().nome(), 40, 560, Color.ORANGE);
        texto("Últimas 10 partidas, da mais recente para a mais antiga", 40, 530, Color.LIGHT_GRAY);
        float y = 490;
        switch (estado) {
            case CARREGANDO -> texto("Carregando...", 40, y, Color.SKY);
            case ERRO -> texto(erro, 40, y, Color.YELLOW);
            case PRONTO -> {
                if (partidas.isEmpty()) {
                    texto("Nenhuma partida enviada ainda.", 40, y, Color.LIGHT_GRAY);
                }
                for (PartidaRegistrada partida : partidas) {
                    texto(FormatoDoHistorico.linha(partida), 40, y, partida.vitoria() ? Color.GREEN : Color.WHITE);
                    y -= 30;
                }
            }
        }
        texto((modoToque() ? "" : "R recarrega     Enter ou Esc: voltar ao menu     ") + jogo.tocador().estado(), 40, 60,
                Color.LIGHT_GRAY);
        batch().end();
    }

    @Override
    protected List<BotaoVirtual> montarBotoes(float largura, float altura) {
        return new LayoutDeOpcoes(400, 10, 390, 90).linha("recarregar", "Recarregar", "voltar", "Voltar").montar();
    }

    /** O mesmo código da tecla: R recarrega, Enter volta. */
    @Override
    protected void aoTocar(String id, int ponteiro) {
        teclaApertada(id.equals("recarregar") ? Keys.R : Keys.ENTER);
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        if (tecla == Keys.R && estado != EstadoDaCarga.CARREGANDO) {
            carregar();
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
