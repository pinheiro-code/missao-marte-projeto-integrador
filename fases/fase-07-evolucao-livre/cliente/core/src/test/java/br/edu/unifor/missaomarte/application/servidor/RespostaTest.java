package br.edu.unifor.missaomarte.application.servidor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RespostaTest {

    @Test
    void okTemValorESemErro() {
        Resposta<String> resposta = Resposta.ok("valor");

        assertTrue(resposta.ok());
        assertEquals("valor", resposta.valor());
        assertNull(resposta.erro());
        assertFalse(resposta.naoAutorizado());
    }

    @Test
    void erroTemMensagemESemValor() {
        Resposta<String> resposta = Resposta.erro("Falhou.");

        assertFalse(resposta.ok());
        assertNull(resposta.valor());
        assertEquals("Falhou.", resposta.erro());
        assertFalse(resposta.naoAutorizado());
    }

    @Test
    void naoAutorizadoTemMensagemEAMarca() {
        Resposta<String> resposta = Resposta.naoAutorizado("Sessão expirada.");

        assertFalse(resposta.ok());
        assertEquals("Sessão expirada.", resposta.erro());
        assertTrue(resposta.naoAutorizado());
    }

    @Test
    void mesmoErroMantemMensagemEMarca() {
        Resposta<Integer> repassada = Resposta.<String>naoAutorizado("Sessão expirada.").mesmoErro();

        assertEquals("Sessão expirada.", repassada.erro());
        assertTrue(repassada.naoAutorizado());
    }

    @Test
    void mesmoErroDeUmaRespostaOkELancaExcecao() {
        assertThrows(IllegalStateException.class, () -> Resposta.ok("valor").mesmoErro());
    }
}
