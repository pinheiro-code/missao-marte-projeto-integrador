package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TremorTest {

    private static final float DELTA = 1e-4f;

    @Test
    void comecaForteDecaiETerminaEmZero() {
        Tremor tremor = new Tremor();
        assertFalse(tremor.ativo(), "parado antes da primeira colisão");

        tremor.iniciar(8, 0.5f);
        assertEquals(8, tremor.amplitude(), DELTA);
        assertTrue(tremor.ativo());

        tremor.atualizar(0.25f);
        assertEquals(4, tremor.amplitude(), DELTA);

        tremor.atualizar(1f);
        assertEquals(0, tremor.amplitude(), DELTA);
        assertEquals(0, tremor.deslocamentoX(), DELTA);
        assertEquals(0, tremor.deslocamentoY(), DELTA);
        assertFalse(tremor.ativo());
    }

    @Test
    void deslocamentoNuncaPassaDaAmplitude() {
        Tremor tremor = new Tremor();
        tremor.iniciar(8, 0.5f);
        for (int i = 0; i < 30; i++) {
            assertTrue(Math.abs(tremor.deslocamentoX()) <= tremor.amplitude() + DELTA);
            assertTrue(Math.abs(tremor.deslocamentoY()) <= tremor.amplitude() + DELTA);
            tremor.atualizar(1 / 60f);
        }
    }
}
