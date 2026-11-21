package br.edu.unifor.missaomarte.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PosicaoTest {

    @Test
    void moverDevolveOutraPosicaoSemMudarAOriginal() {
        Posicao original = new Posicao(1, 2);

        Posicao movida = original.mover(1, -1);

        assertEquals(new Posicao(2, 1), movida);
        assertEquals(new Posicao(1, 2), original);
    }

    @Test
    void igualdadeEHashCodePelasCoordenadas() {
        assertEquals(new Posicao(3, -4), new Posicao(3, -4));
        assertEquals(new Posicao(3, -4).hashCode(), new Posicao(3, -4).hashCode());
        assertNotEquals(new Posicao(3, -4), new Posicao(-4, 3));
    }

    @Test
    void dentroDoMapaVaiDeMenosTamanhoAMaisTamanho() {
        assertTrue(new Posicao(-2, 2).dentroDoMapa(2));
        assertFalse(new Posicao(3, 0).dentroDoMapa(2));
        assertFalse(new Posicao(0, -3).dentroDoMapa(2));
    }
}
