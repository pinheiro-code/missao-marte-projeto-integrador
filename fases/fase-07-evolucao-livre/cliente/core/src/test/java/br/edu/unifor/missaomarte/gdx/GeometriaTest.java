package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Mapa de -5 a 5, células de 48 px a partir de (16, 36). */
class GeometriaTest {

    private final Geometria geometria = new Geometria(5);

    @Test
    void ladoDoMapaTemOnzeCelulas() {
        assertEquals(528, geometria.lado());
    }

    @Test
    void xCresceParaADireita() {
        assertEquals(16, geometria.esquerda(-5));
        assertEquals(256, geometria.esquerda(0));
        assertEquals(496, geometria.esquerda(5));
        assertTrue(geometria.esquerda(1) > geometria.esquerda(0));
    }

    @Test
    void yDoJogoInvertidoNaTela() {
        assertEquals(516, geometria.baixo(-5), "y = -5 é a linha de cima");
        assertEquals(276, geometria.baixo(0));
        assertEquals(36, geometria.baixo(5), "y = 5 é a linha de baixo");
        assertTrue(geometria.baixo(-1) > geometria.baixo(0), "CIMA (y - 1) sobe na tela");
    }

    @Test
    void centroDaCelula() {
        assertEquals(280, geometria.centroX(0));
        assertEquals(300, geometria.centroY(0));
    }
}
