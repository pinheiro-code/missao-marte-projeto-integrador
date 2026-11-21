package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.unifor.missaomarte.application.servidor.ConfiguracaoDoServidor;
import org.junit.jupiter.api.Test;

class FormularioDeServidorTest {

    private final FormularioDeServidor formulario = new FormularioDeServidor();

    private void digitar(String texto) {
        for (char c : texto.toCharArray()) {
            formulario.digitar(c);
        }
    }

    @Test
    void enderecoAceitaLetrasNumerosPontoEHifen() {
        digitar("Meu-PC.192");

        assertEquals("Meu-PC.192", formulario.endereco());
    }

    @Test
    void enderecoRecusaEspacoBarraArrobaEDoisPontos() {
        assertFalse(formulario.digitar(' '));
        assertFalse(formulario.digitar('/'));
        assertFalse(formulario.digitar('@'));
        assertFalse(formulario.digitar(':'));
        assertFalse(formulario.digitar('_'));
        assertEquals("", formulario.endereco());
    }

    @Test
    void enderecoParaNoLimite() {
        for (int i = 0; i < FormularioDeServidor.MAXIMO_ENDERECO + 10; i++) {
            formulario.digitar('a');
        }

        assertEquals(FormularioDeServidor.MAXIMO_ENDERECO, formulario.endereco().length());
    }

    @Test
    void portaSoAceitaDigitosAteCinco() {
        formulario.trocarCampo();
        digitar("8a0-8.0123");

        assertEquals("80801", formulario.porta());
        assertFalse(formulario.digitar('9'));
    }

    @Test
    void apagarMexeSoNoCampoAtual() {
        digitar("abc");
        formulario.selecionarCampo(FormularioDeServidor.Campo.PORTA);
        digitar("12");
        formulario.apagar();

        assertEquals("abc", formulario.endereco());
        assertEquals("1", formulario.porta());
    }

    @Test
    void definirTextoRecusaCaractereForaDaRegraSemMudarOCampo() {
        formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, "192.168.0.10");
        formulario.definirTexto(FormularioDeServidor.Campo.PORTA, "8080");

        assertEquals("Digite só o endereço, sem http:// ou https://.",
                formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, "http://x/a@b"));
        assertEquals("Endereço inválido: use letras, números, hífen e ponto.",
                formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, "meu-servidor.com:8080"));
        assertEquals("O endereço não pode ter espaço, barra nem @.",
                formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, "a b"));
        assertEquals("192.168.0.10", formulario.endereco());
        assertEquals(FormularioDeServidor.Campo.ENDERECO, formulario.campo());

        assertEquals("A porta só aceita números.",
                formulario.definirTexto(FormularioDeServidor.Campo.PORTA, " 80 80 1234"));
        assertEquals("A porta deve ser de 1 a 65535.",
                formulario.definirTexto(FormularioDeServidor.Campo.PORTA, "123456"));
        assertEquals("8080", formulario.porta());
        assertEquals(FormularioDeServidor.Campo.PORTA, formulario.campo());
    }

    @Test
    void definirTextoTiraOsEspacosDasPontas() {
        assertNull(formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, "  Meu-Servidor.local "));
        assertNull(formulario.definirTexto(FormularioDeServidor.Campo.PORTA, " 8080 "));

        assertEquals("Meu-Servidor.local", formulario.endereco());
        assertEquals("8080", formulario.porta());
    }

    @Test
    void definirTextoSubstituiOQueHavia() {
        digitar("antigo");
        formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, "novo");
        formulario.definirTexto(FormularioDeServidor.Campo.PORTA, null);

        assertEquals("novo", formulario.endereco());
        assertEquals("", formulario.porta());
    }

    @Test
    void montarDevolveAConfiguracaoValidada() {
        formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, "192.168.0.10");
        formulario.definirTexto(FormularioDeServidor.Campo.PORTA, "8080");
        formulario.alternarHttps();

        FormularioDeServidor.Montagem montagem = formulario.montar();

        assertTrue(montagem.ok());
        assertNull(montagem.erro());
        assertEquals("https://192.168.0.10:8080", montagem.configuracao().url());
    }

    @Test
    void montarDevolveAMensagemQuandoInvalido() {
        formulario.definirTexto(FormularioDeServidor.Campo.PORTA, "8080");
        assertEquals("Digite o endereço do servidor.", formulario.montar().erro());

        formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, "256.1.1.1");
        assertFalse(formulario.montar().ok());

        formulario.definirTexto(FormularioDeServidor.Campo.ENDERECO, "localhost");
        formulario.definirTexto(FormularioDeServidor.Campo.PORTA, "");
        assertEquals("Digite a porta.", formulario.montar().erro());

        formulario.definirTexto(FormularioDeServidor.Campo.PORTA, "99999");
        assertEquals("A porta deve ser de 1 a 65535.", formulario.montar().erro());
    }

    @Test
    void carregarPreencheComAConfiguracaoEmUso() {
        formulario.carregar(ConfiguracaoDoServidor.criar("jogo.exemplo.com", 443, true));

        assertEquals("jogo.exemplo.com", formulario.endereco());
        assertEquals("443", formulario.porta());
        assertTrue(formulario.https());
    }
}
