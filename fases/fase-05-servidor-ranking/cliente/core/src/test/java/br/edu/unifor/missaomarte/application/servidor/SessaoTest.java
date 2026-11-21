package br.edu.unifor.missaomarte.application.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SessaoTest {

    @Test
    void toStringMostraONomeESemOToken() {
        Sessao sessao = new Sessao("piloto_1", "eyJ.token.secreto");

        String texto = sessao.toString();

        assertTrue(texto.contains("piloto_1"));
        assertFalse(texto.contains("eyJ"));
        assertFalse(texto.contains("secreto"));
    }

    @Test
    void guardaNomeEToken() {
        Sessao sessao = new Sessao("piloto_1", "abc");

        assertEquals("piloto_1", sessao.nome());
        assertEquals("abc", sessao.token());
    }
}
