package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FormularioDeLoginTest {

    private final FormularioDeLogin formulario = new FormularioDeLogin();

    private void digitar(String texto) {
        for (char c : texto.toCharArray()) {
            formulario.digitar(c);
        }
    }

    @Test
    void nomeAceitaLetrasNumerosSublinhadoEHifen() {
        digitar("Piloto_9-x");

        assertEquals("Piloto_9-x", formulario.nome());
    }

    @Test
    void nomeRecusaEspacoAcentoEPontuacao() {
        assertFalse(formulario.digitar(' '));
        assertFalse(formulario.digitar('ã'));
        assertFalse(formulario.digitar('.'));
        assertFalse(formulario.digitar('@'));
        assertFalse(formulario.digitar('\t'));
        assertEquals("", formulario.nome());
    }

    @Test
    void nomeParaEmVinteCaracteres() {
        digitar("abcdefghijklmnopqrstuvwxyz");

        assertEquals(20, formulario.nome().length());
        assertFalse(formulario.digitar('a'));
    }

    @Test
    void senhaAceitaQualquerCaractereVisivelMasNaoControle() {
        formulario.trocarCampo();
        digitar("a\"b\\c ç!");

        assertEquals("a\"b\\c ç!", formulario.senha());
        assertFalse(formulario.digitar('\n'));
        assertFalse(formulario.digitar('\b'));
    }

    @Test
    void senhaParaEmSetentaEDoisBytes() {
        formulario.trocarCampo();
        for (int i = 0; i < 36; i++) {
            formulario.digitar('ç');
        }

        // ç tem 2 bytes em UTF-8: 36 deles são 72 bytes
        assertEquals(36, formulario.senha().length());
        assertFalse(formulario.digitar('a'));
    }

    @Test
    void senhaApareceMascarada() {
        formulario.trocarCampo();
        digitar("segredo1");

        assertEquals("********", formulario.senhaMascarada());
        assertFalse(formulario.toString().contains("segredo1"));
    }

    @Test
    void apagarTiraOUltimoDoCampoAtual() {
        digitar("abc");
        formulario.trocarCampo();
        digitar("xy");

        formulario.apagar();
        assertEquals("x", formulario.senha());
        assertEquals("abc", formulario.nome());

        formulario.apagar();
        formulario.apagar();
        assertEquals("", formulario.senha());
    }

    @Test
    void trocarCampoVaiEVolta() {
        assertEquals(FormularioDeLogin.Campo.NOME, formulario.campo());
        formulario.trocarCampo();
        assertEquals(FormularioDeLogin.Campo.SENHA, formulario.campo());
        formulario.trocarCampo();
        assertEquals(FormularioDeLogin.Campo.NOME, formulario.campo());
    }

    @Test
    void alternarModoTrocaEntreEntrarECriar() {
        assertFalse(formulario.criarConta());
        formulario.alternarModo();
        assertTrue(formulario.criarConta());
        formulario.alternarModo();
        assertFalse(formulario.criarConta());
    }

    @Test
    void entrarSoExigeOsDoisCampos() {
        assertEquals("Digite o nome.", formulario.validar());
        digitar("ab");
        assertEquals("Digite a senha.", formulario.validar());
        formulario.trocarCampo();
        digitar("123");

        assertNull(formulario.validar());
    }

    @Test
    void criarContaExigeNomeDeTresESenhaDeOito() {
        formulario.alternarModo();
        digitar("ab");
        formulario.trocarCampo();
        digitar("1234567");

        assertEquals("O nome deve ter de 3 a 20 caracteres.", formulario.validar());

        formulario.trocarCampo();
        digitar("c");
        assertEquals("A senha deve ter pelo menos 8 caracteres.", formulario.validar());

        formulario.trocarCampo();
        digitar("8");
        assertNull(formulario.validar());
    }
}
