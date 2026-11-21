package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.domain.Acao;
import br.edu.unifor.missaomarte.domain.ElementoMapa;
import br.edu.unifor.missaomarte.domain.Missao;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A partida: traduz tecla em Acao, chama a GameSession e desenha o estado.
 * Não decide regra nenhuma; o fim da partida é só um estado da tela.
 */
public class PartidaTela extends TelaBase {

    private static final String TAG = "Partida";
    private static final float PAINEL_X = 560;
    private static final float PAINEL_LARGURA = 224;
    private static final float INSET = 5;

    private final GameSession sessao;
    private final Missao missao;
    private final Geometria geometria;
    private final TecladoParaAcao teclado = new TecladoParaAcao();
    private final GlyphLayout medida = new GlyphLayout();

    private List<String> mensagens = List.of("Boa missão! Leve todos os passageiros até a base L.");
    private Acao ultimaDirecao = Acao.CIMA;
    private boolean registrado;
    private List<String> resumo;

    /** A tela guarda a Missao que a fábrica criou só para desenhar o mapa; quem muda o estado é a sessão. */
    public PartidaTela(MissaoMarteGame jogo, GameSession sessao, Missao missao) {
        super(jogo);
        this.sessao = sessao;
        this.missao = missao;
        this.geometria = new Geometria(missao.tamanho());
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        if (sessao.acabou()) {
            return teclaNoFim(tecla);
        }
        Acao acao = teclado.converter(tecla);
        if (acao == Acao.INVALIDA) {
            return false;
        }
        if (acao == Acao.SAIR) {
            sessao.jogar(Acao.SAIR);
            Gdx.app.log(TAG, "missão abortada");
            jogo.trocarTela(new MenuTela(jogo));
            return true;
        }
        if (acao.dx() != 0 || acao.dy() != 0) {
            ultimaDirecao = acao;
        }
        mensagens = sessao.jogar(acao).mensagens();
        Gdx.app.log(TAG, acao + " -> pontos " + sessao.pontos() + ", vidas " + sessao.vidas()
                + ", a bordo " + sessao.aBordo() + ", entregues " + sessao.entregues() + "/" + sessao.total());
        if (sessao.acabou() && sessao.venceu()) {
            registrarVitoria();
        }
        return true;
    }

    private boolean teclaNoFim(int tecla) {
        if (tecla != Keys.ENTER && tecla != Keys.NUMPAD_ENTER) {
            return false;
        }
        jogo.trocarTela(sessao.venceu() ? new RankingTela(jogo, resumo) : new MenuTela(jogo));
        return true;
    }

    /** Só uma vez por partida: a flag protege contra outra chamada. */
    private void registrarVitoria() {
        if (registrado) {
            return;
        }
        registrado = true;
        String situacao;
        try {
            boolean entrou = jogo.ranking().registrar(sessao.piloto(), sessao.pontos(), sessao.dificuldade(),
                    sessao.entregues(), sessao.duracaoSegundos());
            situacao = entrou ? "Você entrou no Top 5!" : "Não foi desta vez no Top 5.";
        } catch (UncheckedIOException e) {
            situacao = "Não foi possível salvar o ranking: " + e.getCause().getMessage();
        }
        Gdx.app.log(TAG, "vitória registrada no ranking: " + situacao);
        resumo = new ArrayList<>();
        resumo.add("Piloto: " + sessao.piloto() + " (" + sessao.dificuldade() + ")");
        resumo.add("Pontuação final: " + sessao.pontos());
        resumo.add("Movimentos: " + sessao.movimentos());
        resumo.add("Tempo de jogo: " + sessao.duracaoSegundos() + " s");
        resumo.add("Passageiros resgatados: " + sessao.entregues());
        resumo.add(situacao);
    }

    @Override
    protected void desenhar(float delta) {
        desenharFormas();
        desenharTextos();
        if (sessao.acabou()) {
            desenharFim();
        }
    }

    private void desenharFormas() {
        formas().begin(ShapeType.Filled);
        int t = missao.tamanho();
        for (int x = -t; x <= t; x++) {
            for (int y = -t; y <= t; y++) {
                formas().setColor((x + y) % 2 == 0 ? 0.36f : 0.32f, 0.16f, 0.10f, 1);
                formas().rect(geometria.esquerda(x) + 1, geometria.baixo(y) + 1,
                        Geometria.CELULA - 2, Geometria.CELULA - 2);
            }
        }
        // de trás para frente: a base primeiro, a nave por último (por cima)
        List<ElementoMapa> elementos = missao.elementos();
        for (int i = elementos.size() - 1; i >= 0; i--) {
            ElementoMapa e = elementos.get(i);
            formas().setColor(cor(e.simbolo()));
            formas().rect(geometria.esquerda(e.posicao().x()) + INSET, geometria.baixo(e.posicao().y()) + INSET,
                    Geometria.CELULA - 2 * INSET, Geometria.CELULA - 2 * INSET);
        }
        formas().setColor(0.12f, 0.08f, 0.10f, 1);
        formas().rect(PAINEL_X - 8, 32, PAINEL_LARGURA + 16, 532);
        formas().rect(0, 568, MissaoMarteGame.LARGURA, 32);
        formas().rect(0, 0, MissaoMarteGame.LARGURA, 28);
        formas().end();
    }

    /** Cor pelo símbolo que o próprio elemento informa; símbolo novo cai no magenta. */
    private static Color cor(char simbolo) {
        return switch (simbolo) {
            case '@' -> Color.CYAN;
            case 'L' -> Color.FOREST;
            case '#' -> Color.GRAY;
            case 'X' -> Color.RED;
            case 'P' -> Color.GOLD;
            case 'E' -> Color.ROYAL;
            case 'T' -> Color.WHITE;
            default -> Color.MAGENTA;
        };
    }

    private void desenharTextos() {
        batch().begin();
        List<ElementoMapa> elementos = missao.elementos();
        fonte().setColor(Color.BLACK);
        for (int i = elementos.size() - 1; i >= 0; i--) {
            ElementoMapa e = elementos.get(i);
            if (coberto(elementos, i)) {
                continue;
            }
            String simbolo = String.valueOf(e.simbolo());
            medida.setText(fonte(), simbolo);
            fonte().draw(batch(), simbolo, geometria.centroX(e.posicao().x()) - medida.width / 2,
                    geometria.centroY(e.posicao().y()) + medida.height / 2);
        }

        texto("MISSÃO MARTE  -  piloto " + sessao.piloto(), 16, 590, Color.ORANGE);
        texto("W/A/S/D ou setas: mover    E: embarcar    Q/Esc: abortar", 16, 20, Color.LIGHT_GRAY);

        float y = 552;
        for (String linha : painel()) {
            texto(linha, PAINEL_X, y, Color.WHITE);
            y -= 22;
        }
        y -= 10;
        texto("Último turno:", PAINEL_X, y, Color.ORANGE);
        y -= 22;
        fonte().setColor(Color.LIGHT_GRAY);
        for (String mensagem : mensagens) {
            GlyphLayout bloco = fonte().draw(batch(), mensagem, PAINEL_X, y, PAINEL_LARGURA, Align.left, true);
            y -= bloco.height + 10;
        }
        batch().end();
    }

    /** Outro elemento que vem antes na lista (desenhado depois) está na mesma célula: só o de cima mostra a letra. */
    private static boolean coberto(List<ElementoMapa> elementos, int indice) {
        for (int j = 0; j < indice; j++) {
            if (elementos.get(j).posicao().equals(elementos.get(indice).posicao())) {
                return true;
            }
        }
        return false;
    }

    private List<String> painel() {
        return List.of(
                "Piloto: " + sessao.piloto(),
                "Dificuldade: " + sessao.dificuldade(),
                "Vidas: " + sessao.vidas(),
                "Pontuação: " + sessao.pontos(),
                "A bordo: " + sessao.aBordo() + "/" + sessao.capacidade(),
                "Entregues: " + sessao.entregues() + "/" + sessao.total(),
                "Restantes no mapa: " + sessao.restantes(),
                "Movimentos: " + sessao.movimentos(),
                "Direção: " + ultimaDirecao);
    }

    private void desenharFim() {
        formas().begin(ShapeType.Filled);
        formas().setColor(0.05f, 0.05f, 0.08f, 1);
        formas().rect(96, 230, 368, 140);
        formas().end();
        batch().begin();
        if (sessao.venceu()) {
            texto("VITÓRIA! Missão cumprida.", 130, 340, Color.GREEN);
            texto("Enter: ver o ranking", 130, 280, Color.WHITE);
        } else {
            texto("GAME OVER", 130, 340, Color.RED);
            texto("Enter: voltar ao menu", 130, 280, Color.WHITE);
        }
        batch().end();
    }
}
