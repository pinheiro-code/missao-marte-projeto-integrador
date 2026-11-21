package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.GameSession;
import br.edu.unifor.missaomarte.application.preferencias.CorDaNave;
import br.edu.unifor.missaomarte.domain.Dificuldade;
import br.edu.unifor.missaomarte.domain.Missao;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import java.util.List;

/**
 * Nome do piloto, dificuldade e cor da nave. Enter decola, Esc volta ao menu, Tab troca a cor.
 * No modo convidado o M é letra, não atalho; no modo logado o nome é o da conta, e a dificuldade e a cor se
 * escolhem. A regra do nome mora no NomeDoPiloto.
 * No modo toque: o campo do nome abre a caixa nativa (no celular) e os botões fazem o papel das teclas.
 * A cor fica guardada a cada troca (PreferenciaDaNave): a próxima Nova partida já começa nela.
 */
public class NovaPartidaTela extends TelaBase {

    /** Mapa fixo de -5 a 5 (11x11). */
    static final int TAMANHO_MAPA = 5;
    private static final String NOME_PADRAO = "Piloto Anônimo";

    private final NomeDoPiloto nome = new NomeDoPiloto();
    private Dificuldade dificuldade = Dificuldade.MEDIO;
    private CorDaNave cor;

    public NovaPartidaTela(MissaoMarteGame jogo) {
        super(jogo);
        this.cor = jogo.preferenciaDaNave().atual();
    }

    @Override
    protected boolean aceitaAtalhoDeSom() {
        return jogo.logado();
    }

    @Override
    protected void desenhar(float delta) {
        batch().begin();
        desenharFundo();
        texto("NOVA MISSÃO", 340, 500, Color.ORANGE);
        if (modoToque()) {
            texto("Dificuldade: " + dificuldade, 340, 475, Color.WHITE);
            MundoRenderer.desenharNaveColorida(batch(), recursos(), cor, 600, 455, 40, 0);
            batch().end();
            return;
        }
        texto("Nome do piloto:", 200, 420, Color.LIGHT_GRAY);
        if (jogo.logado()) {
            texto(jogo.sessao().nome() + " (sua conta)", 340, 420, Color.WHITE);
        } else {
            texto(nome.texto() + "_", 340, 420, Color.WHITE);
        }
        texto("Dificuldade:", 200, 370, Color.LIGHT_GRAY);
        texto("< " + dificuldade + " >", 340, 370, Color.WHITE);
        texto("Cor da nave:", 200, 320, Color.LIGHT_GRAY);
        texto(cor.nome() + "   (Tab troca)", 340, 320, Color.WHITE);
        MundoRenderer.desenharNaveColorida(batch(), recursos(), cor, 560, 290, 48, 0);
        texto(jogo.logado() ? "Setas mudam a dificuldade."
                : "Digite o nome (Backspace apaga). Setas mudam a dificuldade.", 200, 280, Color.LIGHT_GRAY);
        texto("Enter decola     Esc volta ao menu", 200, 250, Color.LIGHT_GRAY);
        batch().end();
    }

    @Override
    protected List<BotaoVirtual> montarBotoes(float largura, float altura) {
        return new LayoutDeOpcoes(40, 20, 720, 430)
                .linha("nome", "Nome")
                .linha("menos", "< Dificuldade", "mais", "Dificuldade >")
                .linha("cor", "Cor")
                .linha("decolar", "Decolar", "voltar", "Voltar")
                .montar();
    }

    @Override
    protected String rotulo(BotaoVirtual botao) {
        if (botao.id().equals("cor")) {
            return "Cor da nave: " + cor.nome();
        }
        if (!botao.id().equals("nome")) {
            return botao.rotulo();
        }
        if (jogo.logado()) {
            return "Piloto: " + jogo.sessao().nome() + " (sua conta)";
        }
        return "Nome do piloto: " + nome.texto() + "_";
    }

    @Override
    protected void aoTocar(String id, int ponteiro) {
        switch (id) {
            case "nome" -> pedirNome();
            case "menos" -> mudarDificuldade(-1);
            case "mais" -> mudarDificuldade(1);
            case "cor" -> trocarCor();
            case "decolar" -> decolar();
            default -> jogo.trocarTela(new MenuTela(jogo));
        }
    }

    /** No celular abre a caixa nativa; no desktop o teclado físico já digita no campo. */
    private void pedirNome() {
        if (jogo.logado()) {
            return;
        }
        jogo.plataforma().pedirTexto("Nome do piloto", nome.texto(), false, texto -> {
            // a resposta pode chegar depois de o jogador sair da tela
            if (jogo.getScreen() == this) {
                nome.definirTexto(texto);
            }
        });
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        switch (tecla) {
            case Keys.BACKSPACE -> nome.apagar();
            case Keys.LEFT, Keys.UP -> mudarDificuldade(-1);
            case Keys.RIGHT, Keys.DOWN -> mudarDificuldade(1);
            case Keys.TAB -> trocarCor();
            case Keys.ENTER, Keys.NUMPAD_ENTER -> decolar();
            case Keys.ESCAPE -> jogo.trocarTela(new MenuTela(jogo));
            default -> {
                return false;
            }
        }
        return true;
    }

    /** Letras vêm aqui, já com acento e maiúscula; teclas de controle ficam no teclaApertada. */
    @Override
    protected boolean caractereDigitado(char caractere) {
        return !jogo.logado() && nome.digitar(caractere);
    }

    private void mudarDificuldade(int passo) {
        Dificuldade[] todas = Dificuldade.values();
        dificuldade = todas[(dificuldade.ordinal() + passo + todas.length) % todas.length];
        jogo.tocador().tocar(Som.SELECIONAR);
    }

    /** Grava na hora: fechar o jogo sem decolar já guarda a escolha. */
    private void trocarCor() {
        cor = cor.proxima();
        jogo.preferenciaDaNave().salvar(cor);
        jogo.tocador().tocar(Som.SELECIONAR);
    }

    private void decolar() {
        String piloto = jogo.logado() ? jogo.sessao().nome() : nome.texto().trim();
        if (piloto.isEmpty()) {
            piloto = NOME_PADRAO;
        }
        jogo.tocador().tocar(Som.SELECIONAR);
        Missao missao = jogo.fabrica().criar(dificuldade, TAMANHO_MAPA);
        GameSession sessao = new GameSession(piloto, dificuldade, missao, jogo.random(), jogo.clock());
        jogo.trocarTela(new PartidaTela(jogo, sessao, missao));
    }
}
