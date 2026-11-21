package br.edu.unifor.missaomarte.gdx;

import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoDoServidor;
import br.edu.unifor.missaomarte.application.servidor.Resposta;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import java.util.List;

/**
 * Configuração do servidor (menu, opção 6, ou F3 na tela de entrar): endereço, porta e HTTPS, Testar
 * conexão (GET /api/saude no endereço digitado, antes de salvar), Salvar e Restaurar padrão.
 * Trocar de servidor encerra a sessão: o token só vale no servidor que o emitiu.
 * Teclado: Tab ou setas trocam o campo, F2 liga ou desliga o HTTPS, F5 testa, Enter salva,
 * F8 restaura o padrão, Esc volta. No modo toque, cada um é um botão.
 */
public class ConfigServidorTela extends TelaBase {

    private final FormularioDeServidor formulario = new FormularioDeServidor();
    /** Veio da tela de entrar: o Voltar volta para lá. */
    private final boolean daEntrada;
    private EstadoDaCarga estadoDoTeste;
    private String urlTestada;
    private String erroDoTeste;
    private String mensagem = "";

    public ConfigServidorTela(MissaoMarteGame jogo, boolean daEntrada) {
        super(jogo);
        this.daEntrada = daEntrada;
        formulario.carregar(jogo.endereco().atual());
    }

    /** O M é letra do endereço, não atalho de som. */
    @Override
    protected boolean aceitaAtalhoDeSom() {
        return false;
    }

    @Override
    protected void desenhar(float delta) {
        FormularioDeServidor.Montagem montagem = formulario.montar();
        batch().begin();
        desenharFundo();
        if (modoToque()) {
            texto("CONFIGURAR SERVIDOR", 300, 585, Color.ORANGE);
            desenharSituacao(montagem, 40, 560);
            batch().end();
            return;
        }
        boolean noEndereco = formulario.campo() == FormularioDeServidor.Campo.ENDERECO;
        texto("CONFIGURAR SERVIDOR", 300, 540, Color.ORANGE);
        texto("Endereço:", 200, 440, Color.LIGHT_GRAY);
        texto(formulario.endereco() + (noEndereco ? "_" : ""), 320, 440, noEndereco ? Color.WHITE : Color.LIGHT_GRAY);
        texto("Porta:", 200, 410, Color.LIGHT_GRAY);
        texto(formulario.porta() + (noEndereco ? "" : "_"), 320, 410, noEndereco ? Color.LIGHT_GRAY : Color.WHITE);
        texto("HTTPS:", 200, 380, Color.LIGHT_GRAY);
        texto(formulario.https() ? "ligado" : "desligado", 320, 380, Color.WHITE);
        desenharSituacao(montagem, 200, 340);
        texto("Tab ou setas trocam o campo     F2: HTTPS     F5: testar conexão", 200, 200, Color.LIGHT_GRAY);
        texto("Enter: salvar     F8: restaurar padrão     Esc: voltar", 200, 175, Color.LIGHT_GRAY);
        batch().end();
    }

    /** Servidor em uso, aviso de HTTP, resultado do teste e a última mensagem, de cima para baixo. */
    private void desenharSituacao(FormularioDeServidor.Montagem montagem, float x, float y) {
        texto("Em uso: " + jogo.endereco().atual().url(), x, y, Color.GRAY);
        if (montagem.ok() && montagem.configuracao().exigeAviso()) {
            texto("Aviso: HTTP fora da rede local. A senha e o token iriam sem criptografia.", x, y - 25,
                    Color.YELLOW);
        }
        String teste = textoDoTeste(montagem);
        texto(teste, x, y - 50, estadoDoTeste == EstadoDaCarga.ERRO ? Color.YELLOW : Color.SKY);
        texto(mensagem, x, y - 75, Color.WHITE);
    }

    /** O resultado só vale para o que está no formulário: se o endereço mudou depois do teste, some. */
    private String textoDoTeste(FormularioDeServidor.Montagem montagem) {
        if (estadoDoTeste == null || !montagem.ok() || !montagem.configuracao().url().equals(urlTestada)) {
            return "";
        }
        return switch (estadoDoTeste) {
            case CARREGANDO -> "Testando a conexão com " + urlTestada + "...";
            case PRONTO -> "Conexão OK: o servidor respondeu.";
            case ERRO -> "Falha no teste: " + erroDoTeste;
        };
    }

    @Override
    protected List<BotaoVirtual> montarBotoes(float largura, float altura) {
        return new LayoutDeOpcoes(40, 20, 720, 430)
                .linha("endereco", "Endereço")
                .linha("porta", "Porta", "https", "HTTPS")
                .linha("testar", "Testar conexão", "salvar", "Salvar")
                .linha("restaurar", "Restaurar padrão", "voltar", "Voltar")
                .montar();
    }

    @Override
    protected String rotulo(BotaoVirtual botao) {
        boolean noEndereco = formulario.campo() == FormularioDeServidor.Campo.ENDERECO;
        return switch (botao.id()) {
            case "endereco" -> "Endereço: " + formulario.endereco() + (noEndereco ? "_" : "");
            case "porta" -> "Porta: " + formulario.porta() + (noEndereco ? "" : "_");
            case "https" -> "HTTPS: " + (formulario.https() ? "ligado" : "desligado");
            default -> botao.rotulo();
        };
    }

    @Override
    protected void aoTocar(String id, int ponteiro) {
        switch (id) {
            case "endereco" -> editar(FormularioDeServidor.Campo.ENDERECO, "Endereço do servidor",
                    formulario.endereco());
            case "porta" -> editar(FormularioDeServidor.Campo.PORTA, "Porta", formulario.porta());
            case "https" -> formulario.alternarHttps();
            case "testar" -> testar();
            case "salvar" -> salvar();
            case "restaurar" -> restaurar();
            default -> sair();
        }
    }

    /** Seleciona o campo; no celular, a caixa nativa devolve o texto pelas mesmas regras de digitação. */
    private void editar(FormularioDeServidor.Campo campo, String titulo, String atual) {
        formulario.selecionarCampo(campo);
        jogo.plataforma().pedirTexto(titulo, atual, false, texto -> {
            if (jogo.getScreen() == this) {
                formulario.definirTexto(campo, texto);
            }
        });
    }

    @Override
    protected boolean teclaApertada(int tecla) {
        switch (tecla) {
            case Keys.TAB, Keys.UP, Keys.DOWN -> formulario.trocarCampo();
            case Keys.BACKSPACE -> formulario.apagar();
            case Keys.F2 -> formulario.alternarHttps();
            case Keys.F5 -> testar();
            case Keys.ENTER, Keys.NUMPAD_ENTER -> salvar();
            case Keys.F8 -> restaurar();
            case Keys.ESCAPE -> sair();
            default -> {
                return false;
            }
        }
        return true;
    }

    @Override
    protected boolean caractereDigitado(char caractere) {
        return formulario.digitar(caractere);
    }

    /** Sempre com a URL de uma configuração validada, nunca com o texto cru do campo. */
    private void testar() {
        FormularioDeServidor.Montagem montagem = formulario.montar();
        if (!montagem.ok()) {
            mensagem = montagem.erro();
            return;
        }
        String url = montagem.configuracao().url();
        urlTestada = url;
        estadoDoTeste = EstadoDaCarga.CARREGANDO;
        mensagem = "";
        jogo.servidor().testarConexao(url, resposta -> receberTeste(url, resposta));
    }

    /** Resposta atrasada (outra tela, ou outro endereço testado depois) é ignorada. */
    private void receberTeste(String url, Resposta<Boolean> resposta) {
        if (jogo.getScreen() != this || !url.equals(urlTestada)) {
            return;
        }
        estadoDoTeste = resposta.ok() ? EstadoDaCarga.PRONTO : EstadoDaCarga.ERRO;
        erroDoTeste = resposta.erro();
    }

    private void salvar() {
        FormularioDeServidor.Montagem montagem = formulario.montar();
        if (!montagem.ok()) {
            // endereço vazio ou inválido: nada é salvo
            mensagem = montagem.erro();
            return;
        }
        boolean mudou = jogo.endereco().salvar(montagem.configuracao());
        mensagem = "Salvo: " + montagem.configuracao().url() + "." + encerrarSessaoSe(mudou);
        jogo.tocador().tocar(Som.SELECIONAR);
    }

    private void restaurar() {
        boolean mudou = jogo.endereco().restaurarPadrao();
        ConfiguracaoDoServidor padrao = jogo.endereco().atual();
        formulario.carregar(padrao);
        mensagem = "Padrão restaurado: " + padrao.url() + "." + encerrarSessaoSe(mudou);
        jogo.tocador().tocar(Som.SELECIONAR);
    }

    /** Trocar de servidor encerra a sessão; devolve o complemento da mensagem. */
    private String encerrarSessaoSe(boolean mudou) {
        if (!mudou) {
            return "";
        }
        boolean estavaLogado = jogo.logado();
        jogo.sairDaConta();
        return estavaLogado ? " Você saiu da conta (o login vale só no servidor antigo)." : "";
    }

    private void sair() {
        jogo.trocarTela(daEntrada ? new EntrarTela(jogo) : new MenuTela(jogo));
    }
}
