package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.Resposta;
import br.edu.unifor.missaomarte.application.servidor.Sessao;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

/**
 * Entrar ou criar conta. Tab ou setas trocam o campo, F2 alterna o modo, Enter confirma, Esc volta.
 * As regras do formulário moram no FormularioDeLogin; aqui só teclado, desenho e a chamada ao servidor.
 */
public class EntrarTela extends TelaBase {

    private final FormularioDeLogin formulario = new FormularioDeLogin();
    private boolean aguardando;
    private String mensagem = "";

    public EntrarTela(MissaoMarteGame jogo) {
        super(jogo);
    }

    /** O M é letra do nome ou da senha, não atalho de som. */
    @Override
    protected boolean aceitaAtalhoDeSom() {
        return false;
    }

    @Override
    protected void desenhar(float delta) {
        boolean noNome = formulario.campo() == FormularioDeLogin.Campo.NOME;
        batch().begin();
        desenharFundo();
        texto(formulario.criarConta() ? "CRIAR CONTA" : "ENTRAR", 340, 500, Color.ORANGE);
        texto("Nome:", 200, 420, Color.LIGHT_GRAY);
        texto(formulario.nome() + (noNome ? "_" : ""), 300, 420, noNome ? Color.WHITE : Color.LIGHT_GRAY);
        texto("Senha:", 200, 380, Color.LIGHT_GRAY);
        texto(formulario.senhaMascarada() + (noNome ? "" : "_"), 300, 380, noNome ? Color.LIGHT_GRAY : Color.WHITE);
        texto("Nome: 3 a 20 letras, números, _ ou -.   Senha: 8 ou mais caracteres.", 200, 320, Color.GRAY);
        texto("Tab ou setas trocam o campo.   F2: " + (formulario.criarConta() ? "já tenho conta" : "criar conta"),
                200, 280, Color.LIGHT_GRAY);
        texto("Enter confirma     Esc volta ao menu", 200, 250, Color.LIGHT_GRAY);
        texto(aguardando ? "Aguardando o servidor..." : mensagem, 200, 200, aguardando ? Color.SKY : Color.YELLOW);
        batch().end();
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        switch (tecla) {
            case Keys.ESCAPE -> jogo.trocarTela(new MenuTela(jogo));
            case Keys.TAB, Keys.UP, Keys.DOWN -> formulario.trocarCampo();
            case Keys.F2 -> {
                formulario.alternarModo();
                mensagem = "";
            }
            case Keys.BACKSPACE -> formulario.apagar();
            case Keys.ENTER, Keys.NUMPAD_ENTER -> confirmar();
            default -> {
                return false;
            }
        }
        return true;
    }

    /** Letras chegam aqui; Tab, Enter e Backspace também chegam, mas o formulário recusa controle. */
    @Override
    protected boolean caractereDigitado(char caractere) {
        return formulario.digitar(caractere);
    }

    private void confirmar() {
        if (aguardando) {
            return;
        }
        String erro = formulario.validar();
        if (erro != null) {
            mensagem = erro;
            return;
        }
        aguardando = true;
        jogo.tocador().tocar(Som.SELECIONAR);
        if (formulario.criarConta()) {
            jogo.servidor().cadastrar(formulario.nome(), formulario.senha(), this::receber);
        } else {
            jogo.servidor().entrar(formulario.nome(), formulario.senha(), this::receber);
        }
    }

    private void receber(Resposta<Sessao> resposta) {
        // o jogador pode ter saído com Esc antes da resposta chegar
        if (jogo.getScreen() != this) {
            return;
        }
        aguardando = false;
        if (resposta.ok()) {
            jogo.entrar(resposta.valor());
            jogo.trocarTela(new MenuTela(jogo, "Bem-vindo, " + resposta.valor().nome() + "!"));
        } else {
            // aqui o 401 é usuário ou senha inválidos, não sessão expirada
            mensagem = resposta.erro();
        }
    }
}
