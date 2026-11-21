package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** A posição visual é fracionária; a conversão continua com o Y invertido. */
class GeometriaFracionariaTest {

    private static final float DELTA = 1e-3f;

    private final Geometria geometria = new Geometria(5);

    @Test
    void meiaCelulaEmXAndaMeiaCelulaDePixels() {
        assertEquals(256 + 24, geometria.esquerda(0.5f), DELTA);
        assertEquals(256 - 12, geometria.esquerda(-0.25f), DELTA);
    }

    @Test
    void meiaCelulaParaCimaNoJogoSobeNaTela() {
        // de y = 0 para y = -1 (CIMA), o meio do caminho fica 24 px acima
        assertEquals(276 + 24, geometria.baixo(-0.5f), DELTA);
        assertEquals(geometria.centroY(-1), geometria.centroY(-0.5f) + 24, DELTA);
    }
}
