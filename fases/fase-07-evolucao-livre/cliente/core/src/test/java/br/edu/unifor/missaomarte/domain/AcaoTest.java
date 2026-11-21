package br.edu.unifor.missaomarte.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AcaoTest {

    @Test
    void converteAsTeclasDoJogo() {
        assertEquals(Acao.CIMA, Acao.deTexto("w"));
        assertEquals(Acao.BAIXO, Acao.deTexto("s"));
        assertEquals(Acao.ESQUERDA, Acao.deTexto("a"));
        assertEquals(Acao.DIREITA, Acao.deTexto("d"));
        assertEquals(Acao.EMBARCAR, Acao.deTexto("c"));
        assertEquals(Acao.SAIR, Acao.deTexto("q"));
    }

    @Test
    void valeSoOPrimeiroCaractereEMaiusculaContaComoMinuscula() {
        assertEquals(Acao.DIREITA, Acao.deTexto("D"));
        assertEquals(Acao.DIREITA, Acao.deTexto("dx"));
        assertEquals(Acao.SAIR, Acao.deTexto("  Q "));
    }

    @Test
    void textoQueNaoEComandoViraInvalida() {
        assertEquals(Acao.INVALIDA, Acao.deTexto("x"));
        assertEquals(Acao.INVALIDA, Acao.deTexto(""));
        assertEquals(Acao.INVALIDA, Acao.deTexto("   "));
        assertEquals(Acao.INVALIDA, Acao.deTexto(null));
    }
}
