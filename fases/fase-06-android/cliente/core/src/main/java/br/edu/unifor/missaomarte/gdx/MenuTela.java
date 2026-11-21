package br.edu.unifor.missaomarte.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Menu principal com fundo, nave animada e música. As opções dependem do modo (OpcoesDoMenu):
 * convidado joga com ranking local e pode entrar; logado vê o ranking global e o histórico.
 * A opção 6 (fase 6) abre a configuração do servidor nos dois modos. No modo toque, cada opção
 * vira um botão que chama o mesmo código da tecla.
 */
public class MenuTela extends TelaBase {

    private String aviso;
    private float tempo;

    public MenuTela(MissaoMarteGame jogo) {
        this(jogo, "");
    }

    public MenuTela(MissaoMarteGame jogo, String aviso) {
        super(jogo);
        this.aviso = aviso;
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
        texto(jogo.logado() ? "Piloto: " + jogo.sessao().nome() : "Modo convidado", 340, 380, Color.SKY);
        if (modoToque()) {
            // as opções são os botões; o aviso sobe para não ficar embaixo deles
            texto(aviso, 340, 360, Color.YELLOW);
        } else {
            float linha = 350;
            for (String opcao : OpcoesDoMenu.todas(jogo.logado())) {
                texto(opcao, 340, linha, Color.WHITE);
                linha -= 30;
            }
            texto(aviso, 340, 165, Color.YELLOW);
        }
        texto(jogo.tocador().estado(), 16, 24, Color.GRAY);
        batch().end();
    }

    @Override
    protected List<BotaoVirtual> montarBotoes(float largura, float altura) {
        List<String> opcoes = OpcoesDoMenu.todas(jogo.logado());
        return new LayoutDeOpcoes(100, 20, 600, 320)
                .linha("1", opcoes.get(0), "2", opcoes.get(1))
                .linha("3", opcoes.get(2), "4", opcoes.get(3))
                .linha("5", opcoes.get(4), "6", opcoes.get(5))
                .montar();
    }

    @Override
    protected void aoTocar(String id, int ponteiro) {
        escolher(Integer.parseInt(id));
    }

    /** No menu, o Voltar do Android fecha o jogo, como a opção Sair. */
    @Override
    protected void voltar() {
        Gdx.app.exit();
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        int opcao = opcao(tecla);
        if (opcao == 0) {
            return false;
        }
        escolher(opcao);
        return true;
    }

    private void escolher(int opcao) {
        jogo.tocador().tocar(Som.SELECIONAR);
        if (opcao == 6) {
            jogo.trocarTela(new ConfigServidorTela(jogo, false));
        } else if (jogo.logado()) {
            opcaoLogado(opcao);
        } else {
            opcaoConvidado(opcao);
        }
    }

    private static int opcao(int tecla) {
        return switch (tecla) {
            case Keys.NUM_1, Keys.NUMPAD_1 -> 1;
            case Keys.NUM_2, Keys.NUMPAD_2 -> 2;
            case Keys.NUM_3, Keys.NUMPAD_3 -> 3;
            case Keys.NUM_4, Keys.NUMPAD_4 -> 4;
            case Keys.NUM_5, Keys.NUMPAD_5 -> 5;
            case Keys.NUM_6, Keys.NUMPAD_6 -> 6;
            default -> 0;
        };
    }

    private void opcaoConvidado(int opcao) {
        switch (opcao) {
            case 1 -> jogo.trocarTela(new NovaPartidaTela(jogo));
            case 2 -> jogo.trocarTela(new RankingTela(jogo, null));
            case 3 -> limparRanking();
            case 4 -> jogo.trocarTela(new EntrarTela(jogo));
            default -> Gdx.app.exit();
        }
    }

    private void opcaoLogado(int opcao) {
        switch (opcao) {
            case 1 -> jogo.trocarTela(new NovaPartidaTela(jogo));
            case 2 -> jogo.trocarTela(new RankingTela(jogo, null));
            case 3 -> jogo.trocarTela(new HistoricoTela(jogo));
            case 4 -> {
                jogo.sairDaConta();
                aviso = "Você saiu da conta.";
                // os rótulos dos botões mudam com o modo
                atualizarBotoes();
            }
            default -> Gdx.app.exit();
        }
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
