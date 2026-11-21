package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** O texto que chega da caixa nativa do celular passa pelas mesmas regras da digitação. */
class FormularioDeLoginTextoTest {

    private final FormularioDeLogin formulario = new FormularioDeLogin();

    @Test
    void definirTextoNoNomeDescartaOQueNaoEAceito() {
        formulario.definirTexto(FormularioDeLogin.Campo.NOME, "pilo to@ã_1-x");

        assertEquals("piloto_1-x", formulario.nome());
        assertEquals(FormularioDeLogin.Campo.NOME, formulario.campo());
    }

    @Test
    void definirTextoNoNomeParaEmVinte() {
        formulario.definirTexto(FormularioDeLogin.Campo.NOME, "abcdefghijklmnopqrstuvwxyz");

        assertEquals("abcdefghijklmnopqrst", formulario.nome());
    }

    @Test
    void definirTextoNaSenhaDescartaControleEPassaDos72Bytes() {
        StringBuilder longa = new StringBuilder("a\nb");
        for (int i = 0; i < 40; i++) {
            longa.append('ç');
        }

        formulario.definirTexto(FormularioDeLogin.Campo.SENHA, longa.toString());

        assertEquals(FormularioDeLogin.Campo.SENHA, formulario.campo());
        assertEquals('a', formulario.senha().charAt(0));
        assertEquals('b', formulario.senha().charAt(1));
        assertEquals(72, formulario.senha().getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        assertEquals("*".repeat(formulario.senha().length()), formulario.senhaMascarada());
    }

    @Test
    void definirTextoSubstituiOCampoInteiroENaoMexeNoOutro() {
        formulario.definirTexto(FormularioDeLogin.Campo.NOME, "antigo");
        formulario.definirTexto(FormularioDeLogin.Campo.SENHA, "senha-1234");
        formulario.definirTexto(FormularioDeLogin.Campo.NOME, "novo");
        formulario.definirTexto(FormularioDeLogin.Campo.SENHA, null);

        assertEquals("novo", formulario.nome());
        assertEquals("", formulario.senha());
    }

    @Test
    void selecionarCampoEscolheOndeADigitacaoCai() {
        formulario.selecionarCampo(FormularioDeLogin.Campo.SENHA);
        formulario.digitar('x');
        formulario.selecionarCampo(FormularioDeLogin.Campo.NOME);
        formulario.digitar('y');

        assertEquals("x", formulario.senha());
        assertEquals("y", formulario.nome());
    }
}
