package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.EventoJogo;
import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.ResultadoTurno;
import br.edu.unifor.missaomarte.domain.Acao;
import br.edu.unifor.missaomarte.domain.Missao;
import br.edu.unifor.missaomarte.domain.Posicao;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A partida: traduz tecla em Acao, chama a GameSession e reage aos eventos do turno com som e efeito.
 * Não decide regra nenhuma (nem a colisão): o mundo é do MundoRenderer, o painel é do Hud.
 */
public class PartidaTela extends TelaBase {

    private static final String TAG = "Partida";

    private final GameSession sessao;
    private final Missao missao;
    private final Geometria geometria;
    private final TecladoParaAcao teclado = new TecladoParaAcao();
    private final RepeticaoDeTecla repeticao = new RepeticaoDeTecla();
    private final PosicoesVisuais posicoes = new PosicoesVisuais();
    private final Tremor tremor = new Tremor();
    private final List<EfeitoTemporario> efeitos = new ArrayList<>();
    private final List<TextoFlutuante> textos = new ArrayList<>();
    private final GlyphLayout medida = new GlyphLayout();
    private final MundoRenderer mundo;
    private final Hud hud;

    private EfeitoTemporario flash;
    private EfeitoTemporario piscando;
    private float tempo;
    private List<String> mensagens = List.of("Boa missão! Leve todos os passageiros até a base L.");
    private Acao ultimaDirecao = Acao.CIMA;
    private boolean registrado;
    private boolean saiu;
    private List<String> resumo;

    /** A tela guarda a Missao que a fábrica criou só para desenhar o mapa; quem muda o estado é a sessão. */
    public PartidaTela(MissaoMarteGame jogo, GameSession sessao, Missao missao) {
        super(jogo);
        this.sessao = sessao;
        this.missao = missao;
        this.geometria = new Geometria(missao.tamanho());
        this.mundo = new MundoRenderer(jogo.recursos(), geometria);
        this.hud = new Hud(jogo.recursos(), jogo.fonte(), sessao);
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        // no LWJGL3 o resto das teclas do mesmo quadro ainda chega aqui depois da troca de tela
        if (saiu) {
            return false;
        }
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
            saiu = true;
            jogo.trocarTela(new MenuTela(jogo));
            return true;
        }
        jogarTurno(acao);
        if (movimento(acao) && !sessao.acabou()) {
            repeticao.pressionar(tecla);
        }
        return true;
    }

    @Override
    protected boolean teclaSolta(int tecla) {
        repeticao.soltar(tecla);
        return false;
    }

    private static boolean movimento(Acao acao) {
        return acao.dx() != 0 || acao.dy() != 0;
    }

    /** Um turno completo, igual para a tecla apertada e para cada repetição. */
    private void jogarTurno(Acao acao) {
        if (movimento(acao)) {
            ultimaDirecao = acao;
        }
        int pontosAntes = sessao.pontos();
        ResultadoTurno resultado = sessao.jogar(acao);
        mensagens = resultado.mensagens();
        reagir(resultado.eventos(), sessao.pontos() - pontosAntes);
        Gdx.app.log(TAG, acao + " -> " + resultado.eventos() + " pontos " + sessao.pontos()
                + ", vidas " + sessao.vidas() + ", a bordo " + sessao.aBordo()
                + ", entregues " + sessao.entregues() + "/" + sessao.total());
        if (sessao.acabou()) {
            repeticao.parar();
            if (sessao.venceu()) {
                registrarVitoria();
            }
        }
    }

    /** Resposta visual e sonora a cada evento; a decisão já veio pronta da GameSession. */
    private void reagir(List<EventoJogo> eventos, int variacao) {
        Posicao nave = missao.nave().posicao();
        for (EventoJogo evento : eventos) {
            switch (evento) {
                case MOVIMENTO -> jogo.tocador().tocar(Som.MOVER);
                case EMBARQUE -> {
                    efeitos.add(new EfeitoTemporario(Imagem.BRILHO, nave.x(), nave.y(), 0.5f));
                    jogo.tocador().tocar(Som.EMBARCAR);
                }
                case ENTREGA -> {
                    efeitos.add(new EfeitoTemporario(Imagem.BRILHO, Posicao.ORIGEM.x(), Posicao.ORIGEM.y(), 0.5f));
                    textos.add(new TextoFlutuante("Entregue!", Posicao.ORIGEM.x(), Posicao.ORIGEM.y()));
                    jogo.tocador().tocar(Som.ENTREGAR);
                }
                case COLISAO -> {
                    efeitos.add(new EfeitoTemporario(Imagem.EXPLOSAO, nave.x(), nave.y(), 0.6f));
                    tremor.iniciar(8, 0.5f);
                    flash = new EfeitoTemporario(null, 0, 0, 0.3f);
                    piscando = new EfeitoTemporario(null, 0, 0, 1.0f);
                    jogo.tocador().tocar(Som.COLISAO);
                }
                case VITORIA -> {
                    jogo.tocador().pausarMusica();
                    jogo.tocador().tocar(Som.VITORIA);
                }
                case DERROTA -> {
                    jogo.tocador().pausarMusica();
                    jogo.tocador().tocar(Som.DERROTA);
                }
            }
        }
        if (TextoFlutuante.mostraVariacao(variacao)) {
            textos.add(new TextoFlutuante(TextoFlutuante.formatarVariacao(variacao), nave.x(), nave.y()));
        }
    }

    private boolean teclaNoFim(int tecla) {
        if (tecla != Keys.ENTER && tecla != Keys.NUMPAD_ENTER) {
            return false;
        }
        saiu = true;
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
        Gdx.app.log(TAG, "vitória registrada no ranking com " + sessao.duracaoSegundos() + " s: " + situacao);
        resumo = new ArrayList<>();
        resumo.add("Piloto: " + sessao.piloto() + " (" + sessao.dificuldade() + ")");
        resumo.add("Pontuação final: " + sessao.pontos());
        resumo.add("Movimentos: " + sessao.movimentos());
        resumo.add("Tempo de jogo: " + sessao.duracaoSegundos() + " s");
        resumo.add("Passageiros resgatados: " + sessao.entregues());
        resumo.add(situacao);
    }

    @Override
    public void hide() {
        super.hide();
        repeticao.parar();
    }

    @Override
    protected void desenhar(float delta) {
        atualizar(delta);

        // o tremor mexe só na câmera do mundo; o HUD fica parado
        float dx = tremor.deslocamentoX();
        float dy = tremor.deslocamentoY();
        jogo.camera().translate(dx, dy);
        aplicarProjecao();
        batch().begin();
        desenharFundo();
        boolean naveAcesa = piscando == null || piscando.aceso();
        mundo.desenhar(batch(), missao, posicoes, tempo, ultimaDirecao, naveAcesa, efeitos);
        desenharTextosFlutuantes();
        batch().end();
        jogo.camera().translate(-dx, -dy);
        aplicarProjecao();

        if (flash != null) {
            desenharFlash();
        }
        hud.desenhar(batch(), formas(), sessao, mensagens, jogo.tocador().estado());
        if (sessao.acabou()) {
            desenharFim();
        }
    }

    /** Tempo dos efeitos e repetição de tecla. A lógica nunca espera o desenho. */
    private void atualizar(float delta) {
        tempo += delta;
        if (!sessao.acabou() && !saiu) {
            int tecla = repeticao.atualizar(delta);
            if (tecla != RepeticaoDeTecla.NENHUMA) {
                jogarTurno(teclado.converter(tecla));
            }
        }
        posicoes.atualizar(missao.elementos(), delta);
        tremor.atualizar(delta);
        EfeitoTemporario.atualizarTodos(efeitos, delta);
        EfeitoTemporario.atualizarTodos(textos, delta);
        if (flash != null) {
            flash.atualizar(delta);
            flash = flash.terminou() ? null : flash;
        }
        if (piscando != null) {
            piscando.atualizar(delta);
            piscando = piscando.terminou() ? null : piscando;
        }
    }

    private void desenharTextosFlutuantes() {
        for (TextoFlutuante t : textos) {
            fonte().setColor(1, 1, 0.6f, t.opacidade());
            medida.setText(fonte(), t.texto());
            fonte().draw(batch(), t.texto(), geometria.centroX(t.x()) - medida.width / 2,
                    geometria.centroY(t.yAtual()) + Geometria.CELULA / 2);
        }
    }

    private void desenharFlash() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas().begin(ShapeType.Filled);
        formas().setColor(1, 0, 0, 0.45f * (1 - flash.progresso()));
        formas().rect(Geometria.MAPA_X, Geometria.MAPA_Y, geometria.lado(), geometria.lado());
        formas().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void desenharFim() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        formas().begin(ShapeType.Filled);
        formas().setColor(0.02f, 0.02f, 0.06f, 0.85f);
        formas().rect(96, 230, 368, 140);
        formas().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch().begin();
        if (sessao.venceu()) {
            texto("VITÓRIA! Missão cumprida.", 130, 340, Color.GREEN);
            texto("Tempo: " + sessao.duracaoSegundos() + " s   Pontos: " + sessao.pontos(), 130, 310, Color.WHITE);
            texto("Enter: ver o ranking", 130, 270, Color.WHITE);
        } else {
            texto("GAME OVER", 130, 340, Color.RED);
            texto(mensagens.isEmpty() ? "" : mensagens.get(mensagens.size() - 1), 130, 310, Color.WHITE);
            texto("Enter: voltar ao menu", 130, 270, Color.WHITE);
        }
        batch().end();
    }
}
