package br.edu.unifor.missaomarte.gdx;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BotaoVirtualTest {

    private final BotaoVirtual botao = new BotaoVirtual("ok", "OK", 100, 50, 80, 40);

    @Test
    void contemOPontoDeDentroEAsQuatroBordas() {
        assertTrue(botao.contem(140, 70));
        assertTrue(botao.contem(100, 50));
        assertTrue(botao.contem(180, 90));
        assertTrue(botao.contem(100, 90));
        assertTrue(botao.contem(180, 50));
    }

    @Test
    void naoContemOPontoDeFora() {
        assertFalse(botao.contem(99.9f, 70));
        assertFalse(botao.contem(180.1f, 70));
        assertFalse(botao.contem(140, 49.9f));
        assertFalse(botao.contem(140, 90.1f));
    }

    @Test
    void sobreposicaoSoComAreaEmComum() {
        assertTrue(botao.sobrepoe(new BotaoVirtual("b", "", 170, 80, 50, 50)));
        assertFalse(botao.sobrepoe(new BotaoVirtual("b", "", 180, 50, 50, 40)));
        assertFalse(botao.sobrepoe(new BotaoVirtual("b", "", 100, 90, 80, 40)));
        assertFalse(botao.sobrepoe(new BotaoVirtual("b", "", 300, 300, 10, 10)));
    }
}
